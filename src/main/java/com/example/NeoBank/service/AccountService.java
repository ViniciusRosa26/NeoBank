package com.example.NeoBank.service;

import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.repository.AccountRepository;
import com.example.NeoBank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {


    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AccountEntity createDefaultAccount(UserEntity user) {
        AccountEntity accountEntity = AccountEntity.builder()
                .balance(0.0)
                .diaryLimitPix(BigDecimal.valueOf(1000.00))
                .deposit(BigDecimal.valueOf(1000.00))
                .withdraw(BigDecimal.valueOf(1000.00))
                .nightLimitPix(BigDecimal.valueOf(500.00))
                .enabled(true)
                .user(user)
                .build();

        return accountRepository.save(accountEntity);
    }


}