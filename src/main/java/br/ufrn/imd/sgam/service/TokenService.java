package br.ufrn.imd.sgam.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import br.ufrn.imd.sgam.model.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;

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

    public String validateToken(String token) { // Valida o token - retorna o username se for válido
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("sgam-api")
                .build();

        return verifier.verify(token).getSubject();
    }

    private Instant genExpirationDate() {
        return Instant.now().plusSeconds(2 * 60 * 60); // 2 horas
    }
}
