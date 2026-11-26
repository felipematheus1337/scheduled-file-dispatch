package scheduled_file_dispatch.v1.adapter.out.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;
import scheduled_file_dispatch.v1.application.port.out.TokenPort;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class TokenJwtAdapter implements TokenPort {
    private final JwtEncoder encoder;
    private final String issuer;
    private final Duration ttl;
    public TokenJwtAdapter(JwtEncoder encoder, @Value("${app.security.jwt.issuer}") String issuer,
                           @Value("${app.security.jwt.ttl}") Duration ttl) {
        this.encoder = encoder; this.issuer = issuer; this.ttl = ttl;
    }
    public String emitir(UUID usuarioId) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).subject(usuarioId.toString())
                .issuedAt(agora).expiresAt(agora.plus(ttl)).build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
