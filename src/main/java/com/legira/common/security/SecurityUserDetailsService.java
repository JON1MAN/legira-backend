package com.legira.common.security;

import com.legira.user.AbstractUser;
import com.legira.user.AbstractUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService {
    private final AbstractUserService abstractUserService;

    public UserDetails loadUserByUsername(String email) {
        AbstractUser abstractUser = findAbstractUserByEmail(email);
        return createSecurityUser(abstractUser);
    }

    public AbstractUser findAbstractUserByEmail(String email) {
        return abstractUserService.findByEmail(email);
    }

    public SecurityUser createSecurityUser(AbstractUser abstractUser) {
        return new SecurityUser(abstractUser);
    }
}
