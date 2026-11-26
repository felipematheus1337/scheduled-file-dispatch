package scheduled_file_dispatch.v1.adapter.out.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity @Table(name = "usuarios") @Getter @Setter @NoArgsConstructor
public class UsuarioJpaEntity {
    @Id private UUID id;
    @Column(nullable = false) private String nome;
    @Column(nullable = false, unique = true) private String email;
    @Column(name = "senha_hash", nullable = false) private String senhaHash;
}
