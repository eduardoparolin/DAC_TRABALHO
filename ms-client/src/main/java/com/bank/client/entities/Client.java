package com.bank.client.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "client")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    @Column(length = 500)
    private String rejectionReason;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(length = 100)
    private String complement;

    @Column(nullable = false, length = 8)
    private String zipCode;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(unique = true)
    private Long accountId;

    private Long managerId;

    private OffsetDateTime creationDate;

    private OffsetDateTime approvalDate;

    private OffsetDateTime rejectionDate;

    public enum ClientStatus {
        AGUARDANDO_APROVACAO,
        APROVADO,
        REJEITADO
    }
}
