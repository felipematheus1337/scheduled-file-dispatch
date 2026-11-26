package scheduled_file_dispatch.v1.application.port.out;

import scheduled_file_dispatch.v1.application.domain.Usuario;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    boolean existePorEmail(String email);
    Optional<Usuario> buscarPorEmail(String email);
    Usuario salvar(Usuario usuario);
}
