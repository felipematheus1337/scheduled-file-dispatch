package scheduled_file_dispatch.v1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import java.net.URI;
import java.time.Duration;

@Configuration
public class S3Configuration {
    @Bean
    S3Client s3Client(@Value("${app.storage.s3.endpoint}") String endpoint,
                      @Value("${app.storage.s3.region}") String region,
                      @Value("${app.storage.s3.access-key}") String access,
                      @Value("${app.storage.s3.secret-key}") String secret) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(access, secret)))
                .serviceConfiguration(software.amazon.awssdk.services.s3.S3Configuration.builder()
                        .pathStyleAccessEnabled(true).build())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallTimeout(Duration.ofSeconds(8))
                        .apiCallAttemptTimeout(Duration.ofSeconds(4)).build())
                .build();
    }
}
