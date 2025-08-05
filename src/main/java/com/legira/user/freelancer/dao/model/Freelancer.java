package com.legira.user.freelancer.dao.model;

import com.legira.user.AbstractUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "freelancers")
public class Freelancer extends AbstractUser {
    private String githubUrl;
}
