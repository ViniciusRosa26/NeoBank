package com.example.NeoBank.config;

import com.example.NeoBank.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class TokenProvider {

    // tempo total de vida do token em milissegundos
    @Value("${jwt.expiration}")
    private long expirationTime;

    // chave usada para assinar e validar o JWT
    @Value("${jwt.key}")
    private String secretKey;

    @PostConstruct
    void validateConfiguration() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("JWT_KEY nao configurada. Defina uma chave JWT antes de iniciar a aplicacao.");
        }

        if (secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_KEY invalida. Use uma chave com pelo menos 32 bytes.");
        }
    }


    //gerar um token
    public String generateToken(Authentication authentication) {
        // pega o usuario autenticado pelo Spring e gera um token para ele
        UserDetails user = (UserDetails) authentication.getPrincipal();
        if (user instanceof UserEntity userEntity) {
            return buildToken(userEntity);
        }
        return null;
    }

    public String generateToken(UserEntity userEntity) {
        return buildToken(userEntity);
    }

    private String buildToken(UserEntity userEntity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // subject recebe o identificador do usuario, aqui o email
        return Jwts.builder()
                .subject(userEntity.getEmail())
                .claim("userId", userEntity.getId())
                .claim("role", userEntity.getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

    }

    private SecretKey getSigningKey() {
      // transforma a chave configurada em um formato aceito pela biblioteca JWT
      return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    //validar um token
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        //valida assinatura e expiracao
        //claims eh o corpo do token, onde tem as informacoes do usuario
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        // devolve o subject do token, que neste projeto representa o email do usuario
        return getClaimsFromToken(token).getSubject();
    }

    public long getExpirationTime() {
        // usado para devolver ao cliente o tempo configurado de expiracao do token
        return expirationTime;
    }
}
