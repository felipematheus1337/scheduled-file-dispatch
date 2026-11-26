package scheduled_file_dispatch.v1.adapter.out.s3;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import scheduled_file_dispatch.v1.application.port.out.ArquivoPort;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3ArquivoAdapter implements ArquivoPort {
    private final S3Client s3;
    private final String bucket;
    private final ObservationRegistry observations;
    public S3ArquivoAdapter(S3Client s3, @Value("${app.storage.s3.bucket}") String bucket,
                            ObservationRegistry observations) {
        this.s3 = s3; this.bucket = bucket; this.observations = observations;
    }

    @Retry(name = "s3Storage") @CircuitBreaker(name = "s3Storage")
    public void salvar(String chave, String tipoMime, byte[] conteudo) {
        Observation.createNotStarted("storage.s3.put", observations).observe(() ->
                s3.putObject(PutObjectRequest.builder().bucket(bucket).key(chave)
                        .contentType(tipoMime).build(), RequestBody.fromBytes(conteudo)));
    }

    @Retry(name = "s3Storage") @CircuitBreaker(name = "s3Storage")
    public byte[] baixar(String chave) {
        return Observation.createNotStarted("storage.s3.get", observations).observe(() ->
                s3.getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key(chave).build()).asByteArray());
    }

    @Retry(name = "s3Storage") @CircuitBreaker(name = "s3Storage")
    public void excluir(String chave) {
        Observation.createNotStarted("storage.s3.delete", observations).observe(() ->
                s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(chave).build()));
    }
}
