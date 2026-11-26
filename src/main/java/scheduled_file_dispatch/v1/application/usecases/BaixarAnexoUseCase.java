package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.port.out.ArquivoPort;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BaixarAnexoUseCase {
    public record ArquivoBaixado(String nome, String tipoMime, byte[] conteudo) {}
    private final ConsultarEnvioUseCase consultar;
    private final ArquivoPort arquivos;
    public BaixarAnexoUseCase(ConsultarEnvioUseCase consultar, ArquivoPort arquivos) {
        this.consultar = consultar; this.arquivos = arquivos;
    }
    public ArquivoBaixado executar(UUID usuarioId, UUID envioId, UUID anexoId) {
        var anexo = consultar.executar(usuarioId, envioId).getAnexos().stream()
                .filter(a -> a.getId().equals(anexoId)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Anexo não encontrado"));
        return new ArquivoBaixado(anexo.getNomeOriginal(), anexo.getTipoMime(),
                arquivos.baixar(anexo.getChaveObjeto()));
    }
}
