package scheduled_file_dispatch.v1.adapter.out.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity @Table(name = "envios", indexes = {
        @Index(name = "idx_envios_agendamento", columnList = "status,agendado_para"),
        @Index(name = "idx_envios_usuario", columnList = "usuario_id")
}) @Getter @Setter @NoArgsConstructor
public class EnvioJpaEntity {
    @Id private UUID id;
    @Column(name = "usuario_id", nullable = false) private UUID usuarioId;
    @Column(name = "destinatario_email", nullable = false) private String destinatarioEmail;
    @Column(nullable = false) private String assunto;
    @Column(nullable = false, columnDefinition = "text") private String corpo;
    @Column(name = "criado_em", nullable = false) private Instant criadoEm;
    @Column(name = "agendado_para") private Instant agendadoPara;
    @Column(name = "enviado_em") private Instant enviadoEm;
    @Column(name = "atualizado_em", nullable = false) private Instant atualizadoEm;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private StatusEnvio status;
    @Column(name = "ultimo_erro") private String ultimoErro;
    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnexoJpaEntity> anexos = new ArrayList<>();
}
