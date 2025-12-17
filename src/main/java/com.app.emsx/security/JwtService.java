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
     * Asegura que la clave tenga al menos 64 bytes (512 bits) para HS512.
     */
    @PostConstruct
    public void initKey() {

        if (secretValue == null || secretValue.isBlank()) {
            throw new IllegalStateException("❌ JWT_SECRET no está configurado en Render o application.properties");
        }

        String secret = secretValue.trim();
        byte[] keyBytes;

        try {
            // Intentar decodificar como Base64
            byte[] decodedKey = Decoders.BASE64.decode(secret);
            
            // HS512 requiere al menos 64 bytes (512 bits)
            if (decodedKey.length >= 64) {
                keyBytes = decodedKey;
                System.out.println("🔐 JWT_SECRET cargado como Base64 (" + decodedKey.length * 8 + " bits)");
            } else {
                // Extender la clave a 64 bytes si es más corta
                keyBytes = extendKeyTo64Bytes(decodedKey);
                System.out.println("⚠️ JWT_SECRET Base64 extendido de " + decodedKey.length * 8 + " bits a 512 bits");
            }
            
            this.key = Keys.hmacShaKeyFor(keyBytes);
            return;
            
        } catch (Exception ignored) {
            // No es Base64 → usar como texto plano
        }

        // Si no es Base64, usar la clave como texto plano
        byte[] textBytes = secret.getBytes();
        
        // HS512 requiere al menos 64 bytes (512 bits)
        if (textBytes.length >= 64) {
            keyBytes = textBytes;
            System.out.println("🔐 JWT_SECRET cargado como texto plano (" + textBytes.length * 8 + " bits)");
        } else {
            // Extender la clave a 64 bytes si es más corta
            keyBytes = extendKeyTo64Bytes(textBytes);
            System.out.println("⚠️ JWT_SECRET texto plano extendido de " + textBytes.length * 8 + " bits a 512 bits");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extiende una clave a exactamente 64 bytes (512 bits) para HS512.
     * Repite la clave y la trunca/pad si es necesario.
     */
    private byte[] extendKeyTo64Bytes(byte[] originalKey) {
        byte[] extendedKey = new byte[64]; // 64 bytes = 512 bits
        
        if (originalKey.length == 0) {
            // Si la clave está vacía, usar un valor por defecto
            Arrays.fill(extendedKey, (byte) 0x42);
        } else {
            // Repetir la clave hasta llenar 64 bytes
            for (int i = 0; i < 64; i++) {
                extendedKey[i] = originalKey[i % originalKey.length];
            }
        }
        
        return extendedKey;
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
