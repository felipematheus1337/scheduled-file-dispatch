package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.util.UUID;

public class EditarRascunhoUseCase {
    private final ConsultarEnvioUseCase consultar;
    private final EnvioRepositoryPort envios;
    public EditarRascunhoUseCase(ConsultarEnvioUseCase consultar, EnvioRepositoryPort envios) {
        this.consultar = consultar; this.envios = envios;
    }
    public Envio executar(UUID usuarioId, UUID id, String destinatario, String assunto, String corpo) {
        Envio envio = consultar.executar(usuarioId, id);
        if (envio.getStatus() != StatusEnvio.RASCUNHO)
            throw new OperacaoInvalidaException("Somente rascunhos podem ser editados");
        envio.setDestinatarioEmail(destinatario.strip());
        envio.setAssunto(assunto.strip());
        envio.setCorpo(corpo);
        return envios.salvar(envio);
    }
}
