package scheduled_file_dispatch.v1.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean Queue enviosQueue(@Value("${app.messaging.queue}") String nome) {
        return QueueBuilder.durable(nome).build();
    }
}
