package com.example.NeoBank.entity;

import com.example.NeoBank.enums.PixKeyTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pix_keys")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PixKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String keyValue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PixKeyTypeEnum keyType;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;
}
