package scheduled_file_dispatch.v1.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import scheduled_file_dispatch.v1.adapter.out.jpa.entity.EnvioJpaEntity;
import scheduled_file_dispatch.v1.application.domain.StatusEnvio;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvioSpringRepository extends JpaRepository<EnvioJpaEntity, UUID> {
    @Override @EntityGraph(attributePaths = "anexos")
    Optional<EnvioJpaEntity> findById(UUID id);

    @EntityGraph(attributePaths = "anexos")
    Optional<EnvioJpaEntity> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    @EntityGraph(attributePaths = "anexos")
    List<EnvioJpaEntity> findAllByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);

    @Query("select e.id from EnvioJpaEntity e where e.status = :status and e.agendadoPara <= :agora")
    List<UUID> buscarVencidos(@Param("status") StatusEnvio status, @Param("agora") Instant agora);

    @Modifying(clearAutomatically = true)
    @Query("update EnvioJpaEntity e set e.status = :destino, e.atualizadoEm = :agora " +
           "where e.id = :id and e.status = :origem")
    int transitar(@Param("id") UUID id, @Param("origem") StatusEnvio origem,
                 @Param("destino") StatusEnvio destino, @Param("agora") Instant agora);

    @Modifying(clearAutomatically = true)
    @Query("update EnvioJpaEntity e set e.status = :destino, e.atualizadoEm = :agora " +
           "where e.id = :id and e.status = :origem and e.agendadoPara <= :agora")
    int enfileirar(@Param("id") UUID id, @Param("origem") StatusEnvio origem,
                  @Param("destino") StatusEnvio destino, @Param("agora") Instant agora);

    @Modifying(clearAutomatically = true)
    @Query("update EnvioJpaEntity e set e.status = :destino, e.agendadoPara = null, " +
           "e.atualizadoEm = :agora where e.id = :id and e.status = :origem")
    int cancelar(@Param("id") UUID id, @Param("origem") StatusEnvio origem,
                 @Param("destino") StatusEnvio destino, @Param("agora") Instant agora);

    @Modifying(clearAutomatically = true)
    @Query("update EnvioJpaEntity e set e.status = :destino, e.enviadoEm = :agora, " +
           "e.ultimoErro = null, e.atualizadoEm = :agora where e.id = :id and e.status = :origem")
    int enviado(@Param("id") UUID id, @Param("origem") StatusEnvio origem,
                @Param("destino") StatusEnvio destino, @Param("agora") Instant agora);

    @Modifying(clearAutomatically = true)
    @Query("update EnvioJpaEntity e set e.status = :destino, e.ultimoErro = :motivo, " +
           "e.atualizadoEm = :agora where e.id = :id and e.status = :origem")
    int falha(@Param("id") UUID id, @Param("origem") StatusEnvio origem,
              @Param("destino") StatusEnvio destino, @Param("motivo") String motivo,
              @Param("agora") Instant agora);

    @Modifying(clearAutomatically = true)
    @Query("update EnvioJpaEntity e set e.status = :destino, e.atualizadoEm = :agora " +
           "where e.status = :origem and e.atualizadoEm < :limite")
    int recuperar(@Param("origem") StatusEnvio origem, @Param("destino") StatusEnvio destino,
                  @Param("limite") Instant limite, @Param("agora") Instant agora);

    @Modifying(clearAutomatically = true)
    @Query("update EnvioJpaEntity e set e.status = :destino, e.ultimoErro = :motivo, " +
           "e.atualizadoEm = :agora where e.status = :origem and e.atualizadoEm < :limite")
    int falharAntigos(@Param("origem") StatusEnvio origem, @Param("destino") StatusEnvio destino,
                      @Param("motivo") String motivo, @Param("limite") Instant limite,
                      @Param("agora") Instant agora);
}
