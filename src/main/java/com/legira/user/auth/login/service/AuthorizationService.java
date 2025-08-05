package com.legira.user.auth.login.service;

import com.legira.common.security.SecurityUser;
import com.legira.common.security.jwt.AuthTokens;
import com.legira.common.security.jwt.JwtService;
import com.legira.user.AbstractUser;
import com.legira.user.AbstractUserService;
import com.legira.user.auth.login.controller.dto.UserLoginDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final AbstractUserService abstractUserService;
    private final JwtService jwtService;
    private final AuthenticationProvider authenticationProvider;

    public AuthTokens authorizeUser(UserLoginDTO loginDTO) {
        log.info("Authorizing a user with email: {}", loginDTO);
        assertValidCredentials(loginDTO);
        AbstractUser abstractUser = abstractUserService.findByEmail(loginDTO.getEmail());
        SecurityUser securityUser = new SecurityUser(abstractUser);
        return jwtService.generateAuthTokens(securityUser);
    }

    private void assertValidCredentials(UserLoginDTO loginDTO) {
        try {
            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            log.error("Invalid password or email provided for email: {}", loginDTO.getEmail());
            throw new BadCredentialsException("Invalid email or password provided");
        }
    }
}
