package com.app.emsx.mappers;

import com.app.emsx.dtos.auth.UserResponse;
import com.app.emsx.entities.User;

public class AuthMapper {

    public static UserResponse toUserResponse(User user) {
        // Obtener el primer rol del usuario desde getAuthorities()
        String role = user.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER");
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(role)
                .build();
    }
}