package scheduled_file_dispatch.v1.application.port.out;

public interface SenhaPort {
    String codificar(String senha);
    boolean confere(String senha, String hash);
}
