package scheduled_file_dispatch.v1.adapter.in.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import scheduled_file_dispatch.v1.application.usecases.ProcessarEnvioUseCase;
import java.util.UUID;

@Component @RequiredArgsConstructor
public class EnvioRabbitListener {
    private final ProcessarEnvioUseCase processar;

    @RabbitListener(queues = "${app.messaging.queue}")
    public void receber(String envioId) { processar.executar(UUID.fromString(envioId)); }
}
