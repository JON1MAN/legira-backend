package com.legira.user.client.service.registration;

import com.legira.user.AbstractUser;
import com.legira.user.AbstractUserService;
import com.legira.user.auth.registration.controller.dto.UserRegisterDTO;
import com.legira.user.auth.registration.service.IRegistrationService;
import com.legira.user.client.dao.model.Client;
import com.legira.user.client.dao.repository.ClientRepository;
import com.legira.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientRegistrationService implements IRegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final AbstractUserService abstractUserService;

    @Transactional
    @Override
    public AbstractUser register(UserRegisterDTO dto) {
        log.info("Registering client with email: {}", dto);
        abstractUserService.validateIfEmailAlreadyTaken(dto.getEmail());
        Client client = new Client();
        client.setEmail(dto.getEmail());
        client.setHashedPassword(passwordEncoder.encode(dto.getPassword()));
        client.setUserRole(getHandledRole());
        return clientRepository.save(client);
    }

    @Override
    public UserRole getHandledRole() {
        return UserRole.CLIENT;
    }
}
