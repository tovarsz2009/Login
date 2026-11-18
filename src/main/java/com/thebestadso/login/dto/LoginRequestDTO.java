package com.thebestadso.login.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    /**
     * Nombre del usuario
     */
    private String username;

    /**
     * Clave del usuario
     */
    private String password;
}
