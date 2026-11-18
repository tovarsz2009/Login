package com.thebestadso.login.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thebestadso.login.dto.HttpGlobalResponseDTO;
import com.thebestadso.login.dto.LoginRequestDTO;
import com.thebestadso.login.dto.LoginResponseDTO;
import com.thebestadso.login.dto.RegisterRequestDTO;
import com.thebestadso.login.dto.UserDTO;
import com.thebestadso.login.exception.InvalidCredentialsException;
import com.thebestadso.login.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();

        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (InvalidCredentialsException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("refresh")
    public ResponseEntity<HttpGlobalResponseDTO<LoginResponseDTO>> refreshToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();

        // Un token legal debe existir y empezar con Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setMessage("Token is invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authHeader.substring(7);

        try {
            return ResponseEntity.ok(authService.refreshToken(token));
        } catch (Exception e) {
            response.setMessage("Token is invalid or expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("register")
    public ResponseEntity<HttpGlobalResponseDTO<UserDTO>> register(@RequestBody RegisterRequestDTO request) {
        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
        } catch (IllegalArgumentException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
}
