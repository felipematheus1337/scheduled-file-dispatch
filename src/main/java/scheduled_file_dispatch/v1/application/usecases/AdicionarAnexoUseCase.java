package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.AnexoEnvio;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.port.out.ArquivoPort;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.util.UUID;

public class AdicionarAnexoUseCase {
    private final ConsultarEnvioUseCase consultar;
    private final EnvioRepositoryPort envios;
    private final ArquivoPort arquivos;
    public AdicionarAnexoUseCase(ConsultarEnvioUseCase consultar, EnvioRepositoryPort envios, ArquivoPort arquivos) {
        this.consultar = consultar; this.envios = envios; this.arquivos = arquivos;
    }
    public AnexoEnvio executar(UUID usuarioId, UUID envioId, String nome, String mime, byte[] conteudo) {
        var envio = consultar.executar(usuarioId, envioId);
        if (envio.getStatus() != StatusEnvio.RASCUNHO)
            throw new OperacaoInvalidaException("Anexos só podem ser alterados em rascunhos");
        if (envio.getAnexos().size() >= 3)
            throw new OperacaoInvalidaException("Cada envio aceita até 3 anexos");
        if (conteudo == null || conteudo.length == 0 || conteudo.length > 5 * 1024 * 1024)
            throw new IllegalArgumentException("O arquivo deve ter entre 1 byte e 5 MB");
        if (nome == null) throw new IllegalArgumentException("Nome de arquivo inválido");
        String seguro = nome.replace('\\', '/');
        seguro = seguro.substring(seguro.lastIndexOf('/') + 1);
        if (seguro.isBlank()) throw new IllegalArgumentException("Nome de arquivo inválido");
        UUID anexoId = UUID.randomUUID();
        String chave = usuarioId + "/" + envioId + "/" + anexoId;
        String tipo = (mime == null || mime.isBlank()) ? "application/octet-stream" : mime;
        AnexoEnvio anexo = new AnexoEnvio(anexoId, envioId, seguro, chave, tipo, conteudo.length);
        arquivos.salvar(chave, tipo, conteudo);
        try {
            envio.getAnexos().add(anexo);
            envios.salvar(envio);
        } catch (RuntimeException e) {
            try { arquivos.excluir(chave); } catch (RuntimeException cleanup) { e.addSuppressed(cleanup); }
            throw e;
        }
        return anexo;
    }
}
