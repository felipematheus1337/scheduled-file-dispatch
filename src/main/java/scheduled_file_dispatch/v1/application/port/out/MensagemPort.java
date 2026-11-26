package scheduled_file_dispatch.v1.application.port.out;

import java.util.UUID;

public interface MensagemPort {
    void publicar(UUID envioId);
}
