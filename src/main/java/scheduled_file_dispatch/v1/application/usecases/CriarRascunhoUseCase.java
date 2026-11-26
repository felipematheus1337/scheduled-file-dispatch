package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class CriarRascunhoUseCase {
    private final EnvioRepositoryPort envios;
    public CriarRascunhoUseCase(EnvioRepositoryPort envios) { this.envios = envios; }

    public Envio executar(UUID usuarioId, String destinatario, String assunto, String corpo) {
        return envios.salvar(new Envio(UUID.randomUUID(), usuarioId, destinatario.strip(),
                assunto.strip(), corpo, Instant.now(), null, null, StatusEnvio.RASCUNHO,
                null, new ArrayList<>()));
    }
}
