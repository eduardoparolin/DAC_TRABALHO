package com.bank.manager.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "manager")
@Data
public class Manager {

    @Id
    private Long id;
    private String cpf;
    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private ManagerType type;

    @Column(name = "account_count")
    private Integer accountCount = 0;
}
