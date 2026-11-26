package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.time.Instant;
import java.util.UUID;

public class ReprocessarEnvioUseCase {
    private final ConsultarEnvioUseCase consultar;
    private final EnvioRepositoryPort envios;
    public ReprocessarEnvioUseCase(ConsultarEnvioUseCase consultar, EnvioRepositoryPort envios) {
        this.consultar = consultar; this.envios = envios;
    }
    public Envio executar(UUID usuarioId, UUID id) {
        Envio envio = consultar.executar(usuarioId, id);
        if (envio.getStatus() != StatusEnvio.FALHA)
            throw new OperacaoInvalidaException("Somente falhas podem ser reprocessadas");
        envio.setStatus(StatusEnvio.AGENDADO);
        envio.setAgendadoPara(Instant.now());
        envio.setUltimoErro(null);
        return envios.salvar(envio);
    }
}
