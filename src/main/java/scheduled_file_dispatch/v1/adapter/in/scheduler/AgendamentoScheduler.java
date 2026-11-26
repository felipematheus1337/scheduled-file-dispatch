package scheduled_file_dispatch.v1.adapter.in.scheduler;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import scheduled_file_dispatch.v1.application.usecases.EnfileirarEnviosVencidosUseCase;

@Component @RequiredArgsConstructor
public class AgendamentoScheduler {
    private final EnfileirarEnviosVencidosUseCase enfileirar;
    private final ObservationRegistry observations;

    @Scheduled(cron = "${app.scheduler.cron}")
    public void executar() {
        Observation.createNotStarted("scheduler.envios.vencidos", observations)
                .observe(enfileirar::executar);
    }
}
