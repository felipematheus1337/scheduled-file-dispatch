package scheduled_file_dispatch.v1.adapter.out.mail;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import scheduled_file_dispatch.v1.application.domain.Envio;
import scheduled_file_dispatch.v1.application.port.out.EmailPort;
import java.util.List;

@Component
public class SmtpEmailAdapter implements EmailPort {
    private final JavaMailSender mail;
    private final String remetente;
    private final ObservationRegistry observations;
    public SmtpEmailAdapter(JavaMailSender mail, @Value("${app.mail.from}") String remetente,
                            ObservationRegistry observations) {
        this.mail = mail; this.remetente = remetente; this.observations = observations;
    }
    @CircuitBreaker(name = "smtp")
    public void enviar(Envio envio, List<EmailPort.Anexo> anexos) {
        Observation.createNotStarted("email.smtp.send", observations).observe(() -> {
            MimeMessage mensagem = mail.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
                helper.setFrom(remetente);
                helper.setTo(envio.getDestinatarioEmail());
                helper.setSubject(envio.getAssunto());
                helper.setText(envio.getCorpo(), false);
                for (var anexo : anexos) {
                    helper.addAttachment(anexo.nome(), new ByteArrayResource(anexo.conteudo()), anexo.tipoMime());
                }
            } catch (MessagingException e) {
                throw new IllegalStateException("Não foi possível montar o e-mail", e);
            }
            mail.send(mensagem);
        });
    }
}
