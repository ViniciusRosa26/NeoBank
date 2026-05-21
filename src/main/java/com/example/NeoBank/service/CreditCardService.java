package com.example.NeoBank.service;

import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.CreditCardEntity;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;

    public CreditCardEntity createDefaultCreditCard(UserEntity user, AccountEntity account) {
        Double limitCredit = user.getSalary() * 1.5;

        CreditCardEntity creditCardEntity = CreditCardEntity.builder()
                .number(org.apache.commons.lang3.RandomStringUtils.randomNumeric(16))
                .holderName(user.getName())
                .expirationDate("12/27")
                .cvc(org.apache.commons.lang3.RandomStringUtils.randomNumeric(3))
                .country("BR")
                .limitCredit(limitCredit)
                .accountEntity(account)
                .build();

        return creditCardRepository.save(creditCardEntity);
    }
}
