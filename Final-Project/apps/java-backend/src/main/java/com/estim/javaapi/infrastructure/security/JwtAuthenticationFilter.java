package com.estim.javaapi.infrastructure.security;

import com.estim.javaapi.domain.user.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Bridge between your existing JwtAuthenticationProvider + custom SecurityContext
 * and Spring Security's SecurityContextHolder.
 *
 * If the Authorization header contains a valid JWT, this filter will:
 *   - Delegate validation to JwtAuthenticationProvider
 *   - Read the UserId from com.estim...SecurityContext
 *   - Populate Spring's SecurityContext with an Authentication whose principal is the user UUID
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public JwtAuthenticationFilter(JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        try {
            String authorizationHeader = request.getHeader("Authorization");

            // Let requests without Authorization header pass; SecurityConfig will decide
            if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                // This will validate the token and populate your custom SecurityContext
                jwtAuthenticationProvider.authenticateFromAuthorizationHeader(authorizationHeader);

                Optional<UserId> maybeUserId = SecurityContext.getCurrentUserId();
                if (maybeUserId.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UUID userUuid = maybeUserId.get().value();

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userUuid,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // Clear your custom SecurityContext to avoid leaking between requests
            jwtAuthenticationProvider.clearAuthentication();
        }
    }
}
