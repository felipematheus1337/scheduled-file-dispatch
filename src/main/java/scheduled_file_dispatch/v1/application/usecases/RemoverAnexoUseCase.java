package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import scheduled_file_dispatch.v1.application.port.out.ArquivoPort;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.util.NoSuchElementException;
import java.util.UUID;

public class RemoverAnexoUseCase {
    private final ConsultarEnvioUseCase consultar;
    private final EnvioRepositoryPort envios;
    private final ArquivoPort arquivos;
    public RemoverAnexoUseCase(ConsultarEnvioUseCase consultar, EnvioRepositoryPort envios, ArquivoPort arquivos) {
        this.consultar = consultar; this.envios = envios; this.arquivos = arquivos;
    }
    public void executar(UUID usuarioId, UUID envioId, UUID anexoId) {
        var envio = consultar.executar(usuarioId, envioId);
        if (envio.getStatus() != StatusEnvio.RASCUNHO)
            throw new OperacaoInvalidaException("Anexos só podem ser alterados em rascunhos");
        var anexo = envio.getAnexos().stream().filter(a -> a.getId().equals(anexoId))
                .findFirst().orElseThrow(() -> new NoSuchElementException("Anexo não encontrado"));
        envio.getAnexos().remove(anexo);
        envios.salvar(envio);
        arquivos.excluir(anexo.getChaveObjeto());
    }
}
