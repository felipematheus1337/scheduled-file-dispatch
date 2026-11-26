package scheduled_file_dispatch.v1.application.port.out;

import scheduled_file_dispatch.v1.application.domain.Envio;
import java.util.List;

public interface EmailPort {
    record Anexo(String nome, String tipoMime, byte[] conteudo) {}
    void enviar(Envio envio, List<Anexo> anexos);
}
