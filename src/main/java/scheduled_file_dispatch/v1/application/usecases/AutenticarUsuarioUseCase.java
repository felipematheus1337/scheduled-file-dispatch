package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.port.out.SenhaPort;
import scheduled_file_dispatch.v1.application.port.out.TokenPort;
import scheduled_file_dispatch.v1.application.port.out.UsuarioRepositoryPort;
import scheduled_file_dispatch.v1.application.domain.CredenciaisInvalidasException;
import java.util.Locale;

public class AutenticarUsuarioUseCase {
    private final UsuarioRepositoryPort usuarios;
    private final SenhaPort senhas;
    private final TokenPort tokens;

    public AutenticarUsuarioUseCase(UsuarioRepositoryPort usuarios, SenhaPort senhas, TokenPort tokens) {
        this.usuarios = usuarios;
        this.senhas = senhas;
        this.tokens = tokens;
    }

    public String executar(String email, String senha) {
        var usuario = usuarios.buscarPorEmail(email.strip().toLowerCase(Locale.ROOT))
                .orElseThrow(CredenciaisInvalidasException::new);
        if (!senhas.confere(senha, usuario.getSenhaHash()))
            throw new CredenciaisInvalidasException();
        return tokens.emitir(usuario.getId());
    }
}
