package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class ConsultarEnvioUseCase {
    private final EnvioRepositoryPort envios;
    public ConsultarEnvioUseCase(EnvioRepositoryPort envios) { this.envios = envios; }

    public Envio executar(UUID usuarioId, UUID envioId) {
        return envios.buscar(envioId, usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Envio não encontrado"));
    }
    public List<Envio> listar(UUID usuarioId) { return envios.listar(usuarioId); }
}
