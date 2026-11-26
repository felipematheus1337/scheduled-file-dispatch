package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.time.Instant;
import java.util.UUID;

public class AgendarEnvioUseCase {
    private final ConsultarEnvioUseCase consultar;
    private final EnvioRepositoryPort envios;
    public AgendarEnvioUseCase(ConsultarEnvioUseCase consultar, EnvioRepositoryPort envios) {
        this.consultar = consultar; this.envios = envios;
    }
    public Envio executar(UUID usuarioId, UUID id, Instant quando) {
        Envio envio = consultar.executar(usuarioId, id);
        if (envio.getStatus() != StatusEnvio.RASCUNHO && envio.getStatus() != StatusEnvio.CANCELADO)
            throw new OperacaoInvalidaException("Envio não pode ser agendado nesse estado");
        if (!quando.isAfter(Instant.now())) throw new IllegalArgumentException("Use uma data futura");
        envio.setAgendadoPara(quando);
        envio.setStatus(StatusEnvio.AGENDADO);
        envio.setUltimoErro(null);
        return envios.salvar(envio);
    }
}
