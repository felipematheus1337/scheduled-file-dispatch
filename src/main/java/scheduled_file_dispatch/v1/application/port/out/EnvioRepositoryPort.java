package scheduled_file_dispatch.v1.application.port.out;

import scheduled_file_dispatch.v1.application.domain.Envio;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvioRepositoryPort {
    Envio salvar(Envio envio);
    Optional<Envio> buscar(UUID id, UUID usuarioId);
    Optional<Envio> buscar(UUID id);
    List<Envio> listar(UUID usuarioId);
    void excluir(UUID id);
    List<UUID> vencidos(Instant agora);
    boolean marcarNaFila(UUID id, Instant agora);
    boolean cancelarAgendado(UUID id, Instant agora);
    void devolverParaAgendado(UUID id, Instant agora);
    boolean marcarProcessando(UUID id, Instant agora);
    void marcarEnviado(UUID id, Instant agora);
    void marcarFalha(UUID id, String motivo, Instant agora);
    void recuperarFilaAntiga(Instant limite);
    void falharProcessamentosAntigos(Instant limite);
}
