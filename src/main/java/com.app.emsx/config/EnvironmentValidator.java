package com.app.emsx.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * EnvironmentValidator
 * -----------------------------------------------------
 * Valida que las variables de entorno críticas estén configuradas.
 * Solo loguea advertencias, no detiene la aplicación.
 */
@Slf4j
@Component
@Order(1) // Ejecutar al inicio
public class EnvironmentValidator implements ApplicationRunner {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:}")
    private String datasourcePassword;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Override
    public void run(ApplicationArguments args) {
        log.info("🔍 Validando variables de entorno críticas...");

        boolean hasWarnings = false;

        // Validar SPRING_DATASOURCE_URL
        if (datasourceUrl == null || datasourceUrl.isEmpty() || datasourceUrl.contains("localhost")) {
            log.warn("⚠️  ADVERTENCIA: SPRING_DATASOURCE_URL no está configurada o usa localhost");
            log.warn("   Configurar en Render: SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/dbname");
            hasWarnings = true;
        } else {
            log.info("✅ SPRING_DATASOURCE_URL configurada correctamente");
        }

        // Validar SPRING_DATASOURCE_USERNAME
        if (datasourceUsername == null || datasourceUsername.isEmpty() || "postgres".equals(datasourceUsername)) {
            log.warn("⚠️  ADVERTENCIA: SPRING_DATASOURCE_USERNAME no está configurada o usa valor por defecto");
            log.warn("   Configurar en Render: SPRING_DATASOURCE_USERNAME=<usuario_db>");
            hasWarnings = true;
        } else {
            log.info("✅ SPRING_DATASOURCE_USERNAME configurada correctamente");
        }

        // Validar SPRING_DATASOURCE_PASSWORD
        if (datasourcePassword == null || datasourcePassword.isEmpty() || "postgres".equals(datasourcePassword)) {
            log.warn("⚠️  ADVERTENCIA: SPRING_DATASOURCE_PASSWORD no está configurada o usa valor por defecto");
            log.warn("   Configurar en Render: SPRING_DATASOURCE_PASSWORD=<password_db>");
            hasWarnings = true;
        } else {
            log.info("✅ SPRING_DATASOURCE_PASSWORD configurada correctamente");
        }

        // Validar JWT_SECRET
        if (jwtSecret == null || jwtSecret.isEmpty() || jwtSecret.contains("default-secret-key")) {
            log.warn("⚠️  ADVERTENCIA: JWT_SECRET no está configurada o usa valor por defecto");
            log.warn("   Configurar en Render: JWT_SECRET=<secret_minimo_64_caracteres>");
            log.warn("   Generar con: openssl rand -base64 64");
            hasWarnings = true;
        } else if (jwtSecret.length() < 64) {
            log.warn("⚠️  ADVERTENCIA: JWT_SECRET tiene menos de 64 caracteres (recomendado para HS512)");
            hasWarnings = true;
        } else {
            log.info("✅ JWT_SECRET configurada correctamente ({} caracteres)", jwtSecret.length());
        }

        if (hasWarnings) {
            log.warn("⚠️  Algunas variables de entorno no están configuradas correctamente.");
            log.warn("   La aplicación puede fallar o comportarse de forma inesperada.");
            log.warn("   Revisar la configuración en Render Dashboard → Environment.");
        } else {
            log.info("✅ Todas las variables de entorno críticas están configuradas correctamente");
        }
    }
}

