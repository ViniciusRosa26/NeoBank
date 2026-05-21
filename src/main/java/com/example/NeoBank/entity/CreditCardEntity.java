package com.example.NeoBank.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credit_card_entity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class CreditCardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "credit_card_seq", sequenceName = "credit_card_sequence", allocationSize = 1)
    private Integer id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String holderName;

    @Column(nullable = false)
    private String expirationDate;

    @Column(nullable = false)
    private String cvc;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private Double limitCredit;

    @OneToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;



}
