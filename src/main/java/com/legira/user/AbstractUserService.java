package com.legira.user;

import com.legira.common.service.AbstractService;
import com.legira.user.exceptions.EmailAlreadyExistsException;
import com.legira.user.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AbstractUserService implements AbstractService<AbstractUser> {
    private final AbstractUserRepository abstractUserRepository;

    @Override
    public AbstractUser findById(UUID id) {
        log.info("Fetching user with id: {}", id.toString());
        return abstractUserRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No user found for id: {}", id);
                    return new UserNotFoundException("No user found with id: " + id);
                });
    }

    public AbstractUser findByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return abstractUserRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("No user found with email: {}", email);
                    return new UserNotFoundException("No user found with email: " + email);
                });
    }

    public void validateIfEmailAlreadyTaken(String email) {
        if (existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    String.format("User with email: %s already exists", email)
            );
        }
    }

    private boolean existsByEmail(String email) {
        return abstractUserRepository.existsByEmail(email);
    }
}
