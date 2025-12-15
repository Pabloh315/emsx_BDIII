package com.app.emsx.security;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

/**
 * JwtService
 * -----------------------------------------------------
 * ✔ Genera y valida tokens JWT
 * ✔ Carga la clave desde .env o variables del sistema
 * ✔ Extrae claims, usuario y expiración
 */
@Slf4j
@Service
public class JwtService {

    private final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() // Evita excepción si .env no existe
            .load();

    private Key key;

    /**
     * ✅ Inicializa la clave al iniciar el servicio (producción segura)
     */
    @PostConstruct
    public void initKey() {
        String secret = null;

        // 1️⃣ Intentar leer desde variable de entorno (Render)
        secret = System.getenv("JWT_SECRET");

        // 2️⃣ Intentar leer desde .env (desarrollo local)
        if (secret == null || secret.isBlank()) {
            try {
                secret = dotenv.get("JWT_SECRET");
            } catch (Exception ignored) {
            }
        }

        // 3️⃣ Intentar leer desde application.properties (último recurso)
        if (secret == null || secret.isBlank()) {
            try {
                // Leer desde application.properties usando @Value no es posible aquí,
                // así que usamos un valor por defecto seguro
                secret = "default-secret-key-change-in-production-minimum-32-characters-long-for-security";
                System.out.println("⚠️ Usando JWT_SECRET por defecto. Cambiar en producción!");
            } catch (Exception ignored) {
            }
        }

        // 4️⃣ Si no se encuentra, lanzar error controlado
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("❌ No se encontró JWT_SECRET");
        }

        // 5️⃣ Validar tamaño mínimo (HS512 requiere al menos 512 bits = 64 bytes)
        // Si el secret es texto plano, lo convertimos a bytes directamente
        byte[] keyBytes;
        try {
            // Intentar decodificar como Base64 primero
            keyBytes = Decoders.BASE64.decode(secret.trim());
        } catch (Exception e) {
            // Si falla, tratar como texto plano
            keyBytes = secret.trim().getBytes();
        }

        // Validar que tenga al menos 64 bytes (512 bits) para HS512
        if (keyBytes.length < 64) {
            // Si es muy corto, repetir hasta alcanzar 64 bytes
            byte[] extendedKey = new byte[64];
            for (int i = 0; i < 64; i++) {
                extendedKey[i] = keyBytes[i % keyBytes.length];
            }
            keyBytes = extendedKey;
            System.out.println("⚠️ JWT_SECRET extendido a 64 bytes para HS512");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("🔑 JWT_SECRET cargada correctamente (" + keyBytes.length * 8 + " bits)");
    }

    private Key getSignInKey() {
        if (key == null) {
            initKey(); // fallback si no fue inicializado
        }
        return key;
    }

    // ✅ Extrae el username (subject)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extrae un claim genérico
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Parse completo del token
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expirado", e);
        } catch (JwtException e) {
            throw new RuntimeException("Token inválido", e);
        }
    }

    // ✅ Genera token con claims extra y roles
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 10)) // 10 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // ✅ Valida token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
