package scheduled_file_dispatch.v1.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import scheduled_file_dispatch.v1.application.usecases.AutenticarUsuarioUseCase;
import scheduled_file_dispatch.v1.application.usecases.CadastrarUsuarioUseCase;
import java.util.UUID;

@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    public record Cadastro(@NotBlank String nome, @NotBlank @Email String email,
                           @NotBlank @Size(min = 8, max = 72) String senha) {}
    public record Login(@NotBlank @Email String email, @NotBlank String senha) {}
    public record UsuarioCriado(UUID id) {}
    public record Token(String token, String tipo) {}

    private final CadastrarUsuarioUseCase cadastrar;
    private final AutenticarUsuarioUseCase autenticar;

    @PostMapping("/register")
    public ResponseEntity<UsuarioCriado> cadastrar(@Valid @RequestBody Cadastro request) {
        UUID id = cadastrar.executar(request.nome(), request.email(), request.senha());
        return ResponseEntity.status(201).body(new UsuarioCriado(id));
    }

    @PostMapping("/login")
    public Token entrar(@Valid @RequestBody Login request) {
        return new Token(autenticar.executar(request.email(), request.senha()), "Bearer");
    }
}
