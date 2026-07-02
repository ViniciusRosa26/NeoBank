package com.example.NeoBank.service;

import com.example.NeoBank.dto.TransactionDto;
import com.example.NeoBank.entity.TransactionEntity;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.enums.EnumTypeTransaction;
import com.example.NeoBank.enums.PixKeyTypeEnum;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.repository.AccountRepository;
import com.example.NeoBank.repository.PixKeyRepository;
import com.example.NeoBank.repository.TransactionRepositoty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.example.NeoBank.enums.EnumTypeTransaction.TRANSFER;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {


    @Mock
    private TransactionRepositoty transactionRepositoty;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PixKeyRepository pixKeyRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private TransactionRateLimitService transactionRateLimitService;
    @Mock
    private BalanceWebSocketService balanceWebSocketService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @DisplayName("Quando dto for nulo")
    void createTransactioncase1_dtonulo() {

        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(null));

    }

    @Test
    @DisplayName("Quando tipo for nulo ")
    void createtransactioncase2_nuultype(){

        TransactionDto transaction = new TransactionDto(1590.00, null, "Desc1", 3, "hdsfsd", PixKeyTypeEnum.RANDOM_KEY);
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(transaction));

    }

    @Test
    @DisplayName("quando amount for nulo")
    void createTransacationcase3_nuulamount(){

        TransactionDto transaction = new TransactionDto(null, TRANSFER, "Desc1", 3, "hdsfsd", PixKeyTypeEnum.RANDOM_KEY);
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(transaction));
    }

    @Test
    @DisplayName("quando amount for menor que zero")
    void createTransactioncase4_amountlesszero(){
        TransactionDto transaction = new TransactionDto(-2.0 , TRANSFER, "Desc1", 3, "hdsfsd", PixKeyTypeEnum.RANDOM_KEY);
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(transaction));
    }

    @Test
    @DisplayName("quando n ter id ")
    void createTransactioncase4_semid() {
        TransactionDto transaction = new TransactionDto(-2.0, TRANSFER, "Desc1", null, "hdsfsd", PixKeyTypeEnum.RANDOM_KEY);
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(transaction));

    }

    @Test
    @DisplayName("sem pixkey ")
    void createTransactioncase4_sempixkey() {
        TransactionDto transaction = new TransactionDto(-2.0, TRANSFER, "Desc1", 3, null, PixKeyTypeEnum.RANDOM_KEY);
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(transaction));

    }

    @Test
    @DisplayName("quando n ter tipo do pix  ")
    void createTransactioncase4_withouttypepix() {
        TransactionDto transaction = new TransactionDto(-2.0, TRANSFER, "Desc1", 4, "hdsfsd", null);
        assertThrows(BadRequestException.class, () -> transactionService.createTransaction(transaction));

    }

}

