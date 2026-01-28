package dev.antonio.portifolio.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    JwtToken jwtToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        var token = this.recoverToken(request);

        if (token != null) {
            var destination = jwtToken.validateToken(token);

            System.out.println("TOKEN VALIDADO PARA: " + destination);

            if (destination != null && !destination.isEmpty()) {
                // Adicione ROLE_USER para o Spring saber que este usuário tem "poderes" de usuário autenticado
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

                var authentication = new UsernamePasswordAuthenticationToken(
                        destination,
                        null,
                        authorities); // Passe a lista aqui (antes estava vazia ou nula)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }


    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

        // Verifica se o header existe e se começa com "Bearer " (com espaço)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        // Corta a String a partir do índice 7 (após "Bearer ") e remove espaços extras nas pontas
        return authHeader.substring(7).trim();
    }
}
