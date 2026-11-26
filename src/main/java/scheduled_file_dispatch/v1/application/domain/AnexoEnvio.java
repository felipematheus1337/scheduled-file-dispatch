package scheduled_file_dispatch.v1.application.domain;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnexoEnvio {

    private UUID id;
    private UUID envioId;
    private String nomeOriginal;
    private String chaveObjeto;
    private String tipoMime;
    private long tamanhoBytes;
}
