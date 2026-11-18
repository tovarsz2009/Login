package com.thebestadso.login.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thebestadso.login.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");

        // Un token legal debe existir y empezar con Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(response, "Header Authorization is missing in the request");
            return;
        }

        // Quitamos "Bearer " para quedarnos solo con el jwt
        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            unauthorized(response, "Token is invalid");
            return;
        }

        // Guardamos los claims en la request por si el controller los necesita
        request.setAttribute("username", jwtService.extractSubject(token));
        request.setAttribute("userId", jwtService.extractUserId(token));
        request.setAttribute("rolId", jwtService.extractRolId(token));

        // Todo bien: pasamos al controller u otro filtro
        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    /**
     * Rutas públicas que no requieren jwt
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/v1/auth/");
    }
}
