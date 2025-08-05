package com.legira.common.security.jwt;

import com.legira.common.security.SecurityUserDetailsService;
import com.legira.common.security.jwt.exceptions.JwtExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityUserDetailsService securityUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authentication");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractUsername(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.securityUserDetailsService.loadUserByUsername(email);
                jwtService.setAuthentication(token, userDetails, request);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error("Token expired");
            throw new JwtExpiredException("Token expired, please refresh");
        } catch (MalformedJwtException e) {
            log.error("Unsupported format of JWT");
            throw new MalformedJwtException("Unsupported format of JWT", e);
        } catch (SignatureException e) {
            log.error("Invalid JWT signature provided");
            throw new SignatureException("Invalid JWT signature provided", e);
        } catch (JwtException e) {
            log.error("Unexpected authentication error");
            throw new JwtException("Unexpected authentication error", e);
        }

    }

    private static void validateBearerPrefix(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String authHeader) throws IOException, ServletException {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
        }
    }
}
