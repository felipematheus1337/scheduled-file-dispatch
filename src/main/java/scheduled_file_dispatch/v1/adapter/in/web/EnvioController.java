package scheduled_file_dispatch.v1.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import scheduled_file_dispatch.v1.application.domain.AnexoEnvio;
import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.usecases.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/envios") @RequiredArgsConstructor
public class EnvioController {
    public record DadosEnvio(@NotBlank @Email String destinatarioEmail,
                             @NotBlank String assunto, @NotBlank String corpo) {}
    public record Agendamento(Instant agendadoPara) {}
    public record AnexoResposta(UUID id, String nomeOriginal, String tipoMime, long tamanhoBytes) {
        static AnexoResposta de(AnexoEnvio a) {
            return new AnexoResposta(a.getId(), a.getNomeOriginal(), a.getTipoMime(), a.getTamanhoBytes());
        }
    }
    public record EnvioResposta(UUID id, String destinatarioEmail, String assunto, String corpo,
                                Instant criadoEm, Instant agendadoPara, Instant enviadoEm,
                                StatusEnvio status, String ultimoErro, List<AnexoResposta> anexos) {
        static EnvioResposta de(Envio e) {
            return new EnvioResposta(e.getId(), e.getDestinatarioEmail(), e.getAssunto(), e.getCorpo(),
                    e.getCriadoEm(), e.getAgendadoPara(), e.getEnviadoEm(), e.getStatus(),
                    e.getUltimoErro(), e.getAnexos().stream().map(AnexoResposta::de).toList());
        }
    }

    private final CriarRascunhoUseCase criar;
    private final ConsultarEnvioUseCase consultar;
    private final EditarRascunhoUseCase editar;
    private final ExcluirRascunhoUseCase excluir;
    private final AgendarEnvioUseCase agendar;
    private final CancelarEnvioUseCase cancelar;
    private final ReprocessarEnvioUseCase reprocessar;
    private final AdicionarAnexoUseCase adicionar;
    private final RemoverAnexoUseCase remover;
    private final BaixarAnexoUseCase baixar;

    private UUID usuario(Jwt jwt) { return UUID.fromString(jwt.getSubject()); }

    @PostMapping
    public ResponseEntity<EnvioResposta> criar(@AuthenticationPrincipal Jwt jwt,
                                                @Valid @RequestBody DadosEnvio r) {
        Envio envio = criar.executar(usuario(jwt), r.destinatarioEmail(), r.assunto(), r.corpo());
        return ResponseEntity.created(URI.create("/api/envios/" + envio.getId())).body(EnvioResposta.de(envio));
    }

    @GetMapping
    public List<EnvioResposta> listar(@AuthenticationPrincipal Jwt jwt) {
        return consultar.listar(usuario(jwt)).stream().map(EnvioResposta::de).toList();
    }

    @GetMapping("/{id}")
    public EnvioResposta buscar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return EnvioResposta.de(consultar.executar(usuario(jwt), id));
    }

    @PutMapping("/{id}")
    public EnvioResposta editar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id,
                                @Valid @RequestBody DadosEnvio r) {
        return EnvioResposta.de(editar.executar(usuario(jwt), id,
                r.destinatarioEmail(), r.assunto(), r.corpo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        excluir.executar(usuario(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/agendar")
    public EnvioResposta agendar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id,
                                 @RequestBody Agendamento r) {
        if (r.agendadoPara() == null) throw new IllegalArgumentException("agendadoPara obrigatório");
        return EnvioResposta.de(agendar.executar(usuario(jwt), id, r.agendadoPara()));
    }

    @PostMapping("/{id}/cancelar")
    public EnvioResposta cancelar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return EnvioResposta.de(cancelar.executar(usuario(jwt), id));
    }

    @PostMapping("/{id}/reprocessar")
    public EnvioResposta reprocessar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        return EnvioResposta.de(reprocessar.executar(usuario(jwt), id));
    }

    @PostMapping(value = "/{id}/anexos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnexoResposta> anexar(@AuthenticationPrincipal Jwt jwt,
                                                 @PathVariable UUID id,
                                                 @RequestParam("arquivo") MultipartFile arquivo) throws IOException {
        var anexo = adicionar.executar(usuario(jwt), id, arquivo.getOriginalFilename(),
                arquivo.getContentType(), arquivo.getBytes());
        return ResponseEntity.created(URI.create("/api/envios/" + id + "/anexos/" + anexo.getId()))
                .body(AnexoResposta.de(anexo));
    }

    @GetMapping("/{id}/anexos/{anexoId}")
    public ResponseEntity<byte[]> baixar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id,
                                          @PathVariable UUID anexoId) {
        var arquivo = baixar.executar(usuario(jwt), id, anexoId);
        MediaType tipo;
        try { tipo = MediaType.parseMediaType(arquivo.tipoMime()); }
        catch (IllegalArgumentException e) { tipo = MediaType.APPLICATION_OCTET_STREAM; }
        return ResponseEntity.ok().contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(arquivo.nome(), StandardCharsets.UTF_8).build().toString())
                .body(arquivo.conteudo());
    }

    @DeleteMapping("/{id}/anexos/{anexoId}")
    public ResponseEntity<Void> remover(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id,
                                         @PathVariable UUID anexoId) {
        remover.executar(usuario(jwt), id, anexoId);
        return ResponseEntity.noContent().build();
    }
}
