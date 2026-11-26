package scheduled_file_dispatch.v1.adapter.out.rabbit;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scheduled_file_dispatch.v1.application.port.out.MensagemPort;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RabbitMensagemAdapter implements MensagemPort {
    private final RabbitTemplate rabbit;
    private final String fila;
    public RabbitMensagemAdapter(RabbitTemplate rabbit, @Value("${app.messaging.queue}") String fila) {
        this.rabbit = rabbit; this.fila = fila;
    }
    public void publicar(UUID envioId) {
        CorrelationData confirmacao = new CorrelationData(UUID.randomUUID().toString());
        rabbit.convertAndSend("", fila, envioId.toString(), confirmacao);
        try {
            var confirm = confirmacao.getFuture().get(5, TimeUnit.SECONDS);
            if (!confirm.ack() || confirmacao.getReturned() != null)
                throw new IllegalStateException("Rabbit não confirmou entrega na fila");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Publicação interrompida", e);
        } catch (java.util.concurrent.ExecutionException | java.util.concurrent.TimeoutException e) {
            throw new IllegalStateException("Falha na confirmação Rabbit", e);
        }
    }
}
