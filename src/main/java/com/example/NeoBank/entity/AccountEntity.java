package com.example.NeoBank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Account_entity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(sequenceName = "account_seq", name = "account_sequence", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private Double balance;
    // o saldo so podera ser definido no inicio, por meio de um processo de trazer dinheiro de outra conta ou por meio de um deposito

    @Column(nullable = false)
    private BigDecimal diaryLimitPix;

    @Column(nullable = false)
    private BigDecimal deposit;

    @Column(nullable = false)
    private BigDecimal withdraw;

    @Column(nullable = false)
    private BigDecimal nightLimitPix;

    @Column(nullable = false)
    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;



}
