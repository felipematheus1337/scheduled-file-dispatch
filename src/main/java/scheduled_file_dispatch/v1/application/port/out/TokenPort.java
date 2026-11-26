package scheduled_file_dispatch.v1.application.port.out;

import java.util.UUID;

public interface TokenPort {
    String emitir(UUID usuarioId);
}
