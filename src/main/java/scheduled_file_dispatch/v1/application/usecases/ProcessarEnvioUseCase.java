package scheduled_file_dispatch.v1.application.usecases;

import scheduled_file_dispatch.v1.application.port.out.ArquivoPort;
import scheduled_file_dispatch.v1.application.port.out.EmailPort;
import scheduled_file_dispatch.v1.application.port.out.EnvioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.UUID;

public class ProcessarEnvioUseCase {
    private static final Logger log = LoggerFactory.getLogger(ProcessarEnvioUseCase.class);
    private final EnvioRepositoryPort envios;
    private final ArquivoPort arquivos;
    private final EmailPort email;
    public ProcessarEnvioUseCase(EnvioRepositoryPort envios, ArquivoPort arquivos, EmailPort email) {
        this.envios = envios; this.arquivos = arquivos; this.email = email;
    }
    public void executar(UUID envioId) {
        if (!envios.marcarProcessando(envioId, Instant.now())) return;
        try {
            var envio = envios.buscar(envioId).orElseThrow();
            var anexos = envio.getAnexos().stream()
                    .map(a -> new EmailPort.Anexo(a.getNomeOriginal(), a.getTipoMime(),
                            arquivos.baixar(a.getChaveObjeto()))).toList();
            email.enviar(envio, anexos);
            envios.marcarEnviado(envioId, Instant.now());
        } catch (RuntimeException e) {
            log.warn("Processamento do envio {} falhou: {}", envioId, e.getClass().getSimpleName());
            // Não gravar e-mail, conteúdo ou credenciais no último erro.
            envios.marcarFalha(envioId, e.getClass().getSimpleName(), Instant.now());
        }
    }
}
