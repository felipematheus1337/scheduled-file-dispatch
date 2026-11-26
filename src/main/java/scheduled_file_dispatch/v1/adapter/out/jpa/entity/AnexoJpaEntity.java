package scheduled_file_dispatch.v1.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity @Table(name = "anexos_envio") @Getter @Setter @NoArgsConstructor
public class AnexoJpaEntity {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envio_id", nullable = false)
    private EnvioJpaEntity envio;
    @Column(name = "nome_original", nullable = false) private String nomeOriginal;
    @Column(name = "chave_objeto", nullable = false, unique = true) private String chaveObjeto;
    @Column(name = "tipo_mime", nullable = false) private String tipoMime;
    @Column(name = "tamanho_bytes", nullable = false) private long tamanhoBytes;
}
