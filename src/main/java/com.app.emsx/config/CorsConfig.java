package com.app.emsx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Permitir todas las origenes usando patterns (necesario para app móvil)
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedOrigins(List.of("*"));

        // ✅ Métodos permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // ✅ Headers permitidos - TODOS para evitar problemas
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        // ⚠️ Desactivar credenciales cuando usas "*" en origins
        config.setAllowCredentials(false);

        // ✅ Cache preflight por 1 hora
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
