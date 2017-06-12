package se.olapetersson.tokenizer.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * Created by ola on 2017-05-27.
 */
public class JwtService {
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private byte[] secretKey = "mySecretTest".getBytes();
    public String signPayload(String payload) {

        Key signingKey = new SecretKeySpec(secretKey, signatureAlgorithm.getJcaName());

        long now = System.currentTimeMillis();
        JwtBuilder builder = Jwts.builder().setId(payload).signWith(signatureAlgorithm, signingKey)
                .setExpiration(new Date(now + 3600_000L));

        return builder.compact();
    }

    public String validateToken(String jwtToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwtToken).getBody();
        Date expiration = claims.getExpiration();
        if (new Date(System.currentTimeMillis()).after(expiration)) {
            throw new IllegalArgumentException();
        } else {
            return claims.getId();
        }

    }
}
