package com.thebestadso.login.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thebestadso.login.dto.HttpGlobalResponseDTO;
import com.thebestadso.login.dto.LoginRequestDTO;
import com.thebestadso.login.dto.LoginResponseDTO;
import com.thebestadso.login.dto.RegisterRequestDTO;
import com.thebestadso.login.dto.UserDTO;
import com.thebestadso.login.entity.Roles;
import com.thebestadso.login.entity.Users;
import com.thebestadso.login.exception.InvalidCredentialsException;
import com.thebestadso.login.repository.RolRepository;
import com.thebestadso.login.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * Rol que se asigna a todo usuario que se registra
     */
    private static final String DEFAULT_ROLE = "USER";

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inicio de sesión
     *
     * @throws InvalidCredentialsException si el usuario no existe o la clave no coincide
     */
    public HttpGlobalResponseDTO<LoginResponseDTO> login(LoginRequestDTO request) {
        if (isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Usuario y clave son obligatorios");
        }

        // Mismo error si falla el usuario o la clave, para no revelar cuál de los dos existe
        Users user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        LoginResponseDTO login = new LoginResponseDTO();
        login.setJwt(jwtService.generateToken(user.getId(), user.getUsername(), user.getRolId()));

        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();
        response.setMessage("Inicio de sesión correcto");
        response.setData(login);
        return response;
    }

    /**
     * Método para refresco del token
     */
    public HttpGlobalResponseDTO<LoginResponseDTO> refreshToken(String token) throws Exception {
        HttpGlobalResponseDTO<LoginResponseDTO> response = new HttpGlobalResponseDTO<>();
        LoginResponseDTO refresh = new LoginResponseDTO();

        refresh.setJwt(jwtService.refreshToken(token));
        response.setMessage("Token refrescado correctamente");
        response.setData(refresh);

        return response;
    }

    /**
     * Registro de usuario
     *
     * @throws IllegalArgumentException datos incompletos
     * @throws IllegalStateException    usuario o correo ya en uso
     */
    public HttpGlobalResponseDTO<UserDTO> register(RegisterRequestDTO request) {
        if (isBlank(request.getUsername()) || isBlank(request.getPassword()) || isBlank(request.getEmail())) {
            throw new IllegalArgumentException("Usuario, clave y correo son obligatorios");
        }

        Optional<Users> userFound = userRepository.findByUsername(request.getUsername());
        Optional<Users> emailFound = userRepository.findByEmail(request.getEmail());
        Roles rol = rolRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException("Rol por defecto no existe"));

        if (userFound.isPresent()) {
            throw new IllegalStateException("Nombre de usuario en uso");
        }

        if (emailFound.isPresent()) {
            throw new IllegalStateException("Correo en uso");
        }

        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRolId(rol.getId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setRolId(user.getRolId());

        HttpGlobalResponseDTO<UserDTO> response = new HttpGlobalResponseDTO<>();
        response.setMessage("Usuario registrado correctamente");
        response.setData(userDTO);
        return response;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
