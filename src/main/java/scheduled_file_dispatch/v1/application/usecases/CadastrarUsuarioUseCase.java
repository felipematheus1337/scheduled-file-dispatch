package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.domain.Usuario;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import scheduled_file_dispatch.v1.application.port.out.SenhaPort;
import scheduled_file_dispatch.v1.application.port.out.UsuarioRepositoryPort;
import java.util.Locale;
import java.util.UUID;

public class CadastrarUsuarioUseCase {
    private final UsuarioRepositoryPort usuarios;
    private final SenhaPort senhas;

    public CadastrarUsuarioUseCase(UsuarioRepositoryPort usuarios, SenhaPort senhas) {
        this.usuarios = usuarios;
        this.senhas = senhas;
    }

    public UUID executar(String nome, String email, String senha) {
        String normalizado = email.strip().toLowerCase(Locale.ROOT);
        if (usuarios.existePorEmail(normalizado)) throw new OperacaoInvalidaException("E-mail já cadastrado");
        return usuarios.salvar(new Usuario(UUID.randomUUID(), nome.strip(), normalizado,
                senhas.codificar(senha))).getId();
    }
}
