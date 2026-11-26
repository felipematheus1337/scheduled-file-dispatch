package scheduled_file_dispatch.v1.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scheduled_file_dispatch.v1.adapter.out.jpa.entity.UsuarioJpaEntity;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioSpringRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    boolean existsByEmail(String email);
    Optional<UsuarioJpaEntity> findByEmail(String email);
}
