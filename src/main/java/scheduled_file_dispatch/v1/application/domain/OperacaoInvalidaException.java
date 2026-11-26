package scheduled_file_dispatch.v1.application.domain;

public class OperacaoInvalidaException extends RuntimeException {
    public OperacaoInvalidaException(String mensagem) { super(mensagem); }
}

