package com.app.emsx.dtos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponseData
 * -----------------------------------------------------
 * ✅ DTO para la estructura de datos en la respuesta de login
 * ✅ Contiene el usuario y el token JWT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseData {
    
    /**
     * Información del usuario autenticado
     */
    private UserInfo user;
    
    /**
     * Token JWT generado
     */
    private String token;
    
    /**
     * Información del usuario
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
    }
}

