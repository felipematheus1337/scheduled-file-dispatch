package scheduled_file_dispatch.v1.adapter.out.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import scheduled_file_dispatch.v1.adapter.out.jpa.entity.UsuarioJpaEntity;
import scheduled_file_dispatch.v1.adapter.out.jpa.repository.UsuarioSpringRepository;
import scheduled_file_dispatch.v1.application.domain.Usuario;
import scheduled_file_dispatch.v1.application.port.out.UsuarioRepositoryPort;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class UsuarioJpaAdapter implements UsuarioRepositoryPort {
    private final UsuarioSpringRepository repository;

    public boolean existePorEmail(String email) { return repository.existsByEmail(email); }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email).map(this::paraDominio);
    }

    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setId(usuario.getId());
        entity.setNome(usuario.getNome());
        entity.setEmail(usuario.getEmail());
        entity.setSenhaHash(usuario.getSenhaHash());
        return paraDominio(repository.save(entity));
    }

    private Usuario paraDominio(UsuarioJpaEntity e) {
        return new Usuario(e.getId(), e.getNome(), e.getEmail(), e.getSenhaHash());
    }
}
