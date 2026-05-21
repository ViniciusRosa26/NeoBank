package com.example.NeoBank.repository;

import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.CreditCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardRepository extends JpaRepository<CreditCardEntity, Integer> {
}
