package com.legira.user.auth.registration.service;

import com.legira.user.AbstractUser;
import com.legira.user.auth.registration.controller.dto.UserRegisterDTO;
import com.legira.user.enums.UserRole;

public interface IRegistrationService {
    AbstractUser register(UserRegisterDTO dto);
    UserRole getHandledRole();
}
