package scheduled_file_dispatch.v1.application.port.out;

public interface ArquivoPort {
    void salvar(String chave, String tipoMime, byte[] conteudo);
    byte[] baixar(String chave);
    void excluir(String chave);
}
