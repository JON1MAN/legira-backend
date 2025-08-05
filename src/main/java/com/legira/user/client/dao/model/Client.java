package com.legira.user.client.dao.model;

import com.legira.user.AbstractUser;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "clients")
public class Client extends AbstractUser {
    private String nip;
}
