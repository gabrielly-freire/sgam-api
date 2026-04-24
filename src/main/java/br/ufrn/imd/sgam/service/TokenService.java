package br.ufrn.imd.sgam.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import br.ufrn.imd.sgam.model.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret:minha-senha-secreta}")
    private String secret;

    public String generateToken(UserInfo user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("sgam-api")
                .withSubject(user.getUsername()) // Guarda o username no token
                .withExpiresAt(genExpirationDate())
                .sign(algorithm);
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
