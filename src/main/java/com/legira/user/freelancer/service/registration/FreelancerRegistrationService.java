package com.legira.user.freelancer.service.registration;

import com.legira.user.AbstractUser;
import com.legira.user.AbstractUserService;
import com.legira.user.auth.registration.controller.dto.UserRegisterDTO;
import com.legira.user.auth.registration.service.IRegistrationService;
import com.legira.user.enums.UserRole;
import com.legira.user.freelancer.dao.model.Freelancer;
import com.legira.user.freelancer.dao.repository.FreelancerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreelancerRegistrationService implements IRegistrationService {
    private final PasswordEncoder passwordEncoder;
    private final FreelancerRepository freelancerRepository;
    private final AbstractUserService abstractUserService;

    @Transactional
    @Override
    public AbstractUser register(UserRegisterDTO dto) {
        log.info("Registering freelancer with email: {}", dto.getEmail());
        abstractUserService.validateIfEmailAlreadyTaken(dto.getEmail());
        Freelancer freelancer = new Freelancer();
        freelancer.setEmail(dto.getEmail());
        freelancer.setHashedPassword(passwordEncoder.encode(dto.getPassword()));
        freelancer.setUserRole(getHandledRole());
        return freelancerRepository.save(freelancer);
    }

    @Override
    public UserRole getHandledRole() {
        return UserRole.FREELANCER;
    }
}
