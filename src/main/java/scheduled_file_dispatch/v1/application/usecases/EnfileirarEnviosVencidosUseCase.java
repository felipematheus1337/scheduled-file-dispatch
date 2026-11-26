package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import scheduled_file_dispatch.v1.application.port.out.MensagemPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.Instant;

public class EnfileirarEnviosVencidosUseCase {
    private static final Logger log = LoggerFactory.getLogger(EnfileirarEnviosVencidosUseCase.class);
    private final EnvioRepositoryPort envios;
    private final MensagemPort mensagens;
    public EnfileirarEnviosVencidosUseCase(EnvioRepositoryPort envios, MensagemPort mensagens) {
        this.envios = envios; this.mensagens = mensagens;
    }
    public void executar() {
        Instant agora = Instant.now();
        envios.falharProcessamentosAntigos(agora.minus(Duration.ofMinutes(15)));
        envios.recuperarFilaAntiga(agora.minus(Duration.ofMinutes(5)));
        for (var id : envios.vencidos(agora)) {
            if (!envios.marcarNaFila(id, agora)) continue;
            try {
                mensagens.publicar(id);
            } catch (RuntimeException e) {
                log.warn("Publicação do envio {} falhou: {}", id, e.getClass().getSimpleName());
                envios.devolverParaAgendado(id, Instant.now());
                // Isola o erro de um envio para que os outros continuem neste ciclo.
            }
        }
    }
}
