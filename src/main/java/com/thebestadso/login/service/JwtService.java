package com.thebestadso.login.service;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio que contiene los métodos para crear y validar un jwt
 */
@Service
public class JwtService {

    /**
     * Clave secreta (Base64)
     */
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    /**
     * Expiración del token en milisegundos
     */
    @Value("${security.jwt.token-expiration}")
    private Long tokenExpiration;

    /**
     * Transforma la clave secreta (BASE64) a un objeto SecretKey
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generar jwt firmado
     */
    public String generateToken(Long userId, String username, Long rolId) {
        return Jwts.builder()
                .claim("userId", userId) // Claims personalizados
                .claim("rolId", rolId)
                .subject(username) // A quién pertenece el token
                .issuedAt(new Date()) // Fecha de creación
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration)) // Fecha de expiración
                .signWith(getSignKey())
                .compact(); // xxxxx.yyyyy.zzzzz
    }

    /**
     * Verifica si un token es válido, no ha sido manipulado y no ha expirado
     */
    public Boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Método genérico para extraer claims
     */
    private <T> T extractClaims(String token, Function<Claims, T> resolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return resolver.apply(claims);
    }

    public String extractSubject(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaims(token, claims -> toLong(claims.get("userId")));
    }

    public Long extractRolId(String token) {
        return extractClaims(token, claims -> toLong(claims.get("rolId")));
    }

    /**
     * Jackson devuelve los números pequeños como Integer, por eso se convierte
     * a través de Number
     */
    private Long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    /**
     * Crea un token nuevo a partir de uno todavía válido
     */
    public String refreshToken(String token) throws Exception {
        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new Exception("Token is expired", e);
        } catch (JwtException e) {
            throw new Exception("Token is invalid", e);
        } catch (Exception e) {
            throw new Exception("Internal server error", e);
        }

        return generateToken(toLong(claims.get("userId")), claims.getSubject(), toLong(claims.get("rolId")));
    }
}
