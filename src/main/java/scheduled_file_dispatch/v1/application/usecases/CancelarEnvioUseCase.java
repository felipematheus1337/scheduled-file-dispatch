package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.util.UUID;

public class CancelarEnvioUseCase {
    private final ConsultarEnvioUseCase consultar;
    private final EnvioRepositoryPort envios;
    public CancelarEnvioUseCase(ConsultarEnvioUseCase consultar, EnvioRepositoryPort envios) {
        this.consultar = consultar; this.envios = envios;
    }
    public Envio executar(UUID usuarioId, UUID id) {
        Envio envio = consultar.executar(usuarioId, id);
        if (envio.getStatus() != StatusEnvio.AGENDADO)
            throw new OperacaoInvalidaException("Somente envios agendados podem ser cancelados");
        if (!envios.cancelarAgendado(id, java.time.Instant.now()))
            throw new OperacaoInvalidaException("O scheduler já retirou o envio da agenda");
        return consultar.executar(usuarioId, id);
    }
}
