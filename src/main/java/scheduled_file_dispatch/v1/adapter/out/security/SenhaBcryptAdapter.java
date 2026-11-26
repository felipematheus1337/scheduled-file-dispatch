package scheduled_file_dispatch.v1.adapter.out.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import scheduled_file_dispatch.v1.application.port.out.SenhaPort;

@Component @RequiredArgsConstructor
public class SenhaBcryptAdapter implements SenhaPort {
    private final PasswordEncoder encoder;
    public String codificar(String senha) { return encoder.encode(senha); }
    public boolean confere(String senha, String hash) { return encoder.matches(senha, hash); }
}
