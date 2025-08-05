package com.legira.common.service;

import com.legira.user.auth.registration.controller.dto.UserRegisterDTO;

import java.util.UUID;

public interface AbstractService<T> {
    T findById(UUID userId);
}
