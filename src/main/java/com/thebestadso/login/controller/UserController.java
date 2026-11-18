package com.thebestadso.login.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thebestadso.login.dto.HttpGlobalResponseDTO;
import com.thebestadso.login.dto.UserDTO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Ruta protegida de ejemplo: solo responde si el JwtValidationFilter dejó pasar la request
 */
@RestController
@RequestMapping("/users/")
public class UserController {

    @GetMapping("me")
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> me(HttpServletRequest request) {
        UserDTO user = new UserDTO();
        user.setId((Long) request.getAttribute("userId"));
        user.setUsername((String) request.getAttribute("username"));
        user.setRolId((Long) request.getAttribute("rolId"));

        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();
        response.setMessage("Usuario autenticado");
        response.setData(user);
        return ResponseEntity.ok(response);
    }
}
