package com.legira.user;

import com.legira.common.entity.AbstractEntity;
import com.legira.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractUser extends AbstractEntity {
    private String firstName;
    private String lastName;
    private UserRole userRole;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "hashed_password")
    private String hashedPassword;
}
