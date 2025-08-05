package com.legira.common.security.jwt;

import com.legira.common.security.SecurityUser;
import com.legira.common.security.SecurityUserDetailsService;
import com.legira.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private final SecurityUserDetailsService securityUserDetailsService;
    private long accessTokenExpirationInMinutes = 1000L * 60 * 60;
    private long refreshTokenExpirationInDays = 1000L * 60 * 60 * 24 * 30;;

    public AuthTokens generateAuthTokens(SecurityUser securityUser) {
        String accessToken = generateToken(securityUser, false);
        String refreshToken = generateToken(securityUser, true);
        return AuthTokens.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public UserRole extractRole(String token) {
        Claims claims = extractAllClaims(token);
        List<String> role = claims.get("roles", List.class);
        return UserRole.valueOf(role.getFirst());
    }

    public String generateToken(UserDetails userDetails, boolean isRefresh) {
        return generateToken(new HashMap<>(), userDetails, isRefresh);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails, boolean isRefresh) {
        generateRolesClaim(claims, userDetails);
        generateIdClaim(claims, userDetails);
        return buildToken(claims, userDetails, isRefresh);
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails, boolean isRefresh) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(isRefresh ?
                        new Date(System.currentTimeMillis() + refreshTokenExpirationInDays) :
                        new Date(System.currentTimeMillis() + accessTokenExpirationInMinutes)
                )
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private static void generateIdClaim(Map<String, Object> claims, UserDetails userDetails) {
        if (userDetails instanceof SecurityUser) {
            claims.put("id", ((SecurityUser) userDetails).getAbstractUser().getId());
        }
    }

    private static void generateRolesClaim(Map<String, Object> claims, UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("roles", roles);
    }

    public AuthTokens refreshTokens(String refreshToken) {
        try {
            String email = extractUsername(refreshToken);

            if (email != null) {
                UserDetails userDetails = this.securityUserDetailsService.loadUserByUsername(email);
                if (isTokenValid(refreshToken, userDetails)) {
                    return generateAuthTokens((SecurityUser) userDetails);
                }
            }
            log.warn("Invalid or expired refresh token for email: {}", email);
            throw new IllegalStateException("Invalid or expired refresh token");
        } catch (Exception exception) {
            log.error("Error refreshing tokens: {}", exception.getMessage(), exception);
            throw new IllegalStateException("Invalid refresh token", exception);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = extractAllClaims(token);

        List<String> roles = claims.get("roles", List.class);

        Collection<? extends GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }

    public void setAuthentication(String token, UserDetails userDetails, HttpServletRequest request) {
        if (isTokenValid(token, userDetails)) {
            Authentication authentication = getAuthentication(token);
            UsernamePasswordAuthenticationToken authToken = buildDAOAuthToken(userDetails, authentication);

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    private static UsernamePasswordAuthenticationToken buildDAOAuthToken(UserDetails userDetails, Authentication authentication) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authentication.getAuthorities()
        );
    }
}
