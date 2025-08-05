package com.legira.user.auth.login.controller;

import com.legira.common.security.jwt.AuthTokens;
import com.legira.user.auth.login.controller.dto.UserLoginDTO;
import com.legira.user.auth.login.service.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity<AuthTokens> loginUser(
            @RequestBody UserLoginDTO request
    ) {
        log.info("Received a request to login user with email: {}", request.getEmail());
        return ResponseEntity.ok(
                authorizationService.authorizeUser(request)
        );
    }
}
