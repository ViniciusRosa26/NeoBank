package com.example.NeoBank.service;

import com.example.NeoBank.repository.AccountRepository;
import com.example.NeoBank.repository.PixKeyRepository;
import com.example.NeoBank.repository.TransactionRepositoty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    @Mock
    private  TransactionRepositoty transactionRepositoty;
    @Mock
    private  AccountRepository accountRepository;
    @Mock
    private  PixKeyRepository pixKeyRepository;
    @Mock
    private  AuthenticatedUserService authenticatedUserService;
    @Mock
    private  TransactionRateLimitService transactionRateLimitService;
    @Mock
    private BalanceWebSocketService balanceWebSocketService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    @DisplayName("Quando pode")
    void createTransactioncase1() {




    }

    @Test
    @DisplayName("Quando n pode")
    void createTransactioncase2() {
    }
}