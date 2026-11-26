package scheduled_file_dispatch.v1.application.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Envio {

    private UUID id;
    private UUID usuarioId;
    private String destinatarioEmail;
    private String assunto;
    private String corpo;
    private Instant criadoEm;
    private Instant agendadoPara;
    private Instant enviadoEm;
    private StatusEnvio status;
    private String ultimoErro;

    private List<AnexoEnvio> anexos = new ArrayList<>();
}
