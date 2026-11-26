package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import scheduled_file_dispatch.v1.application.port.out.ArquivoPort;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.util.UUID;

public class ExcluirRascunhoUseCase {
    private final ConsultarEnvioUseCase consultar;
    private final EnvioRepositoryPort envios;
    private final ArquivoPort arquivos;
    public ExcluirRascunhoUseCase(ConsultarEnvioUseCase consultar, EnvioRepositoryPort envios, ArquivoPort arquivos) {
        this.consultar = consultar; this.envios = envios; this.arquivos = arquivos;
    }
    public void executar(UUID usuarioId, UUID id) {
        var envio = consultar.executar(usuarioId, id);
        if (envio.getStatus() != StatusEnvio.RASCUNHO)
            throw new OperacaoInvalidaException("Somente rascunhos podem ser excluídos");
        // O banco é a fonte da verdade; a limpeza no S3 é posterior e pode ser repetida.
        envios.excluir(id);
        envio.getAnexos().forEach(anexo -> arquivos.excluir(anexo.getChaveObjeto()));
    }
}
