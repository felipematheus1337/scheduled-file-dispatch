package scheduled_file_dispatch.v1.application.domain;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() { super("Credenciais inválidas"); }
}
