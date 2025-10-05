package com.alten.ecommerce_api.security;

import com.alten.ecommerce_api.model.dto.Authent.UtilisateurPrincipalDto;
import com.alten.ecommerce_api.service.ServiceJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FiltreAuthentificationJWT extends OncePerRequestFilter {

    private final ServiceJWT serviceJWT;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String enTeteAutorisation = request.getHeader("Authorization");

        if (Objects.isNull(enTeteAutorisation) || !enTeteAutorisation.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = enTeteAutorisation.substring(7);
        final String email = serviceJWT.extraireEmail(jwt);
        final Boolean estAdmin = serviceJWT.extraireEstAdmin(jwt);

        if (Objects.nonNull(email) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            if (serviceJWT.validerToken(jwt, email)) {
                List<SimpleGrantedAuthority> autorites = new ArrayList<>();
                if (Boolean.TRUE.equals(estAdmin)) {
                    autorites.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
                autorites.add(new SimpleGrantedAuthority("ROLE_USER"));

                UtilisateurPrincipalDto principal = new UtilisateurPrincipalDto(email, estAdmin);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        autorites
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}