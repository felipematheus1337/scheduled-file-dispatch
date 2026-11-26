package scheduled_file_dispatch.v1.application.domain;

import java.util.UUID;

public class AnexoEnvio {

    private UUID id;
    private UUID envioId;
    private String nomeOriginal;
    private String chaveObjeto;
    private String tipoMime;
    private long tamanhoBytes;
}
