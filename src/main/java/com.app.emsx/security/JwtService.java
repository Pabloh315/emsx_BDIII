package com.app.emsx.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

/**
 * JwtService
 * -----------------------------------------------------
 * ✔ Carga JWT_SECRET desde variables de entorno o application.properties
 * ✔ Acepta claves Base64 y claves normales
 * ✔ No falla si el Base64 es inválido
 * ✔ Compatible con Render, Docker y entornos productivos
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretValue; // viene de JWT_SECRET (desde variables de entorno en Render)

    private Key key;

    /**
     * Inicializa la clave al iniciar la aplicación.
     * No lanza errores si la clave no es Base64.
     */
    @PostConstruct
    public void initKey() {

        if (secretValue == null || secretValue.isBlank()) {
            throw new IllegalStateException("❌ JWT_SECRET no está configurado en Render o application.properties");
        }

        String secret = secretValue.trim();

        try {
            // Intentar decodificar como Base64
            byte[] decodedKey = Decoders.BASE64.decode(secret);
            if (decodedKey.length >= 32) {
                this.key = Keys.hmacShaKeyFor(decodedKey);
                System.out.println("🔐 JWT_SECRET cargado como Base64 (" + decodedKey.length * 8 + " bits)");
                return;
            }
        } catch (Exception ignored) {
            // No es Base64 → intentar como texto normal
        }

        // Si no es Base64, usar la clave como texto plano (válido también)
        if (secret.length() < 32) {
            System.out.println("⚠ Advertencia: JWT_SECRET es corto. Debe tener ≥ 32 caracteres para HS512.");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        System.out.println("🔐 JWT_SECRET cargado como texto plano (" + secret.length() + " chars)");
    }

    private Key getSignInKey() {
        if (key == null) {
            initKey();
        }
        return key;
    }

    // EXTRAER USERNAME
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // EXTRAER CLAIM
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // PARSEAR TOKEN COMPLETO
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // GENERAR JWT
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // VALIDAR TOKEN
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
