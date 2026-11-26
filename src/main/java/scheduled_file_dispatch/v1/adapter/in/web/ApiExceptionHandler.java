package scheduled_file_dispatch.v1.adapter.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import scheduled_file_dispatch.v1.application.domain.CredenciaisInvalidasException;
import scheduled_file_dispatch.v1.application.domain.OperacaoInvalidaException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.IOException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    public record Erro(String mensagem) {}

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Erro> naoEncontrado(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Erro(e.getMessage()));
    }
    @ExceptionHandler({OperacaoInvalidaException.class, DataIntegrityViolationException.class})
    ResponseEntity<Erro> conflito(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Erro("Operação não permitida ou dados duplicados"));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Erro> invalido(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new Erro(e.getMessage()));
    }
    @ExceptionHandler(CredenciaisInvalidasException.class)
    ResponseEntity<Erro> credenciais(CredenciaisInvalidasException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Erro("Credenciais inválidas"));
    }
    @ExceptionHandler({RuntimeException.class, IOException.class})
    ResponseEntity<Erro> interno(Exception e) {
        log.error("Falha na API", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Erro("Erro interno"));
    }
}
