package com.legira.user.auth.registration.controller;

import com.legira.common.security.jwt.AuthTokens;
import com.legira.user.auth.registration.controller.dto.UserRegisterDTO;
import com.legira.user.auth.registration.service.RegistrationService;
import com.legira.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<AuthTokens> registerUser(
            @RequestBody UserRegisterDTO request,
            @RequestParam(name = "role") UserRole userRole
    ) {
        log.info("Registering user with email: {}", request.getEmail());
        return ResponseEntity.ok(
                registrationService.register(request, userRole)
        );
    }
}
