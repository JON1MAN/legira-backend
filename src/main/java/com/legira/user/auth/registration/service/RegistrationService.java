package com.legira.user.auth.registration.service;

import com.legira.common.security.SecurityUser;
import com.legira.common.security.jwt.AuthTokens;
import com.legira.common.security.jwt.JwtService;
import com.legira.user.AbstractUser;
import com.legira.user.auth.registration.controller.dto.UserRegisterDTO;
import com.legira.user.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegistrationService {
    private final JwtService jwtService;
    private final Map<UserRole, IRegistrationService> registrationServiceMap;

    public RegistrationService(
            JwtService jwtService,
            List<IRegistrationService> registrationServices
    ) {
        this.jwtService = jwtService;
        this.registrationServiceMap = registrationServices.stream()
                .collect(Collectors.toMap(IRegistrationService::getHandledRole, service -> service));
    }

    @Transactional
    public AuthTokens register(UserRegisterDTO registerDTO, UserRole userRole) {
        log.info("Registering user with email: {} for role: {}",
                registerDTO.getEmail(), userRole);
        AbstractUser abstractUser = createUser(registerDTO, userRole);
        SecurityUser securityUser = new SecurityUser(abstractUser);

        return jwtService.generateAuthTokens(securityUser);
    }

    @Transactional
    public AbstractUser createUser(UserRegisterDTO registerDTO, UserRole userRole) {
        IRegistrationService registrationService = registrationServiceMap.get(userRole);
        return registrationService.register(registerDTO);
    }
}
