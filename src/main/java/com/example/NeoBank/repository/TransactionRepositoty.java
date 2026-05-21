package com.example.NeoBank.repository;

import com.example.NeoBank.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepositoty extends JpaRepository<TransactionEntity, Integer> {



}
