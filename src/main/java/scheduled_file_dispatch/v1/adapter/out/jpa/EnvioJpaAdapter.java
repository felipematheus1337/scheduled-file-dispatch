package scheduled_file_dispatch.v1.adapter.out.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import scheduled_file_dispatch.v1.adapter.out.jpa.entity.AnexoJpaEntity;
import scheduled_file_dispatch.v1.adapter.out.jpa.entity.EnvioJpaEntity;
import scheduled_file_dispatch.v1.adapter.out.jpa.repository.EnvioSpringRepository;
import scheduled_file_dispatch.v1.application.domain.AnexoEnvio;
import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository @RequiredArgsConstructor
public class EnvioJpaAdapter implements EnvioRepositoryPort {
    private final EnvioSpringRepository repository;

    @Transactional
    public Envio salvar(Envio envio) {
        EnvioJpaEntity e = repository.findById(envio.getId()).orElseGet(EnvioJpaEntity::new);
        e.setId(envio.getId());
        e.setUsuarioId(envio.getUsuarioId());
        e.setDestinatarioEmail(envio.getDestinatarioEmail());
        e.setAssunto(envio.getAssunto());
        e.setCorpo(envio.getCorpo());
        e.setCriadoEm(envio.getCriadoEm());
        e.setAgendadoPara(envio.getAgendadoPara());
        e.setEnviadoEm(envio.getEnviadoEm());
        e.setStatus(envio.getStatus());
        e.setUltimoErro(envio.getUltimoErro());
        e.setAtualizadoEm(Instant.now());
        Map<UUID, AnexoEnvio> desejados = envio.getAnexos().stream()
                .collect(Collectors.toMap(AnexoEnvio::getId, a -> a));
        e.getAnexos().removeIf(a -> !desejados.containsKey(a.getId()));
        Map<UUID, AnexoJpaEntity> atuais = new HashMap<>();
        e.getAnexos().forEach(a -> atuais.put(a.getId(), a));
        for (AnexoEnvio a : envio.getAnexos()) {
            AnexoJpaEntity item = atuais.get(a.getId());
            if (item == null) {
                item = new AnexoJpaEntity();
                item.setId(a.getId());
                item.setEnvio(e);
                e.getAnexos().add(item);
            }
            item.setNomeOriginal(a.getNomeOriginal());
            item.setChaveObjeto(a.getChaveObjeto());
            item.setTipoMime(a.getTipoMime());
            item.setTamanhoBytes(a.getTamanhoBytes());
        }
        return paraDominio(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Optional<Envio> buscar(UUID id, UUID usuarioId) {
        return repository.findByIdAndUsuarioId(id, usuarioId).map(this::paraDominio);
    }
    @Transactional(readOnly = true)
    public Optional<Envio> buscar(UUID id) { return repository.findById(id).map(this::paraDominio); }
    @Transactional(readOnly = true)
    public List<Envio> listar(UUID usuarioId) {
        return repository.findAllByUsuarioIdOrderByCriadoEmDesc(usuarioId).stream().map(this::paraDominio).toList();
    }
    public void excluir(UUID id) { repository.deleteById(id); }

    @Transactional(readOnly = true)
    public List<UUID> vencidos(Instant agora) {
        return repository.buscarVencidos(StatusEnvio.AGENDADO, agora);
    }

    @Transactional
    public boolean marcarNaFila(UUID id, Instant agora) {
        return repository.enfileirar(id, StatusEnvio.AGENDADO, StatusEnvio.NA_FILA, agora) == 1;
    }
    @Transactional
    public boolean cancelarAgendado(UUID id, Instant agora) {
        return repository.cancelar(id, StatusEnvio.AGENDADO, StatusEnvio.CANCELADO, agora) == 1;
    }
    @Transactional
    public void devolverParaAgendado(UUID id, Instant agora) {
        repository.transitar(id, StatusEnvio.NA_FILA, StatusEnvio.AGENDADO, agora);
    }
    @Transactional
    public boolean marcarProcessando(UUID id, Instant agora) {
        return repository.transitar(id, StatusEnvio.NA_FILA, StatusEnvio.PROCESSANDO, agora) == 1;
    }
    @Transactional
    public void marcarEnviado(UUID id, Instant agora) {
        repository.enviado(id, StatusEnvio.PROCESSANDO, StatusEnvio.ENVIADO, agora);
    }
    @Transactional
    public void marcarFalha(UUID id, String motivo, Instant agora) {
        repository.falha(id, StatusEnvio.PROCESSANDO, StatusEnvio.FALHA, motivo, agora);
    }
    @Transactional
    public void recuperarFilaAntiga(Instant limite) {
        repository.recuperar(StatusEnvio.NA_FILA, StatusEnvio.AGENDADO, limite, Instant.now());
    }
    @Transactional
    public void falharProcessamentosAntigos(Instant limite) {
        repository.falharAntigos(StatusEnvio.PROCESSANDO, StatusEnvio.FALHA,
                "Processamento interrompido; reprocessar manualmente", limite, Instant.now());
    }

    private Envio paraDominio(EnvioJpaEntity e) {
        List<AnexoEnvio> anexos = new ArrayList<>();
        for (AnexoJpaEntity a : e.getAnexos()) {
            anexos.add(new AnexoEnvio(a.getId(), e.getId(), a.getNomeOriginal(),
                    a.getChaveObjeto(), a.getTipoMime(), a.getTamanhoBytes()));
        }
        return new Envio(e.getId(), e.getUsuarioId(), e.getDestinatarioEmail(), e.getAssunto(),
                e.getCorpo(), e.getCriadoEm(), e.getAgendadoPara(), e.getEnviadoEm(),
                e.getStatus(), e.getUltimoErro(), anexos);
    }
}
