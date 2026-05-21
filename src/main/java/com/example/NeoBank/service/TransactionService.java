package com.example.NeoBank.service;

import com.example.NeoBank.dto.TransactionDto;
import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.TransactionEntity;
import com.example.NeoBank.enums.EnumTypeTransaction;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.exception.NotFoundException;
import com.example.NeoBank.repository.AccountRepository;
import com.example.NeoBank.repository.TransactionRepositoty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepositoty transactionRepositoty;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionEntity createTransaction(Integer accountId, TransactionDto transactionDto) {
        validateTransactionDto(transactionDto);
        validateAccountId(accountId);

        return switch (transactionDto.typeTransaction()) {
            case DEPOSIT -> processDeposit(accountId, transactionDto);
            case WITHDRAW -> processWithdraw(accountId, transactionDto);
            case TRANSFER -> processTransfer(accountId, transactionDto);
        };
    }

    private TransactionEntity processDeposit(Integer accountId, TransactionDto transactionDto) {
        AccountEntity originAccount = getAccountById(accountId);
        originAccount.setBalance(originAccount.getBalance() + transactionDto.amount());
        accountRepository.save(originAccount);

        return saveTransaction(
                transactionDto,
                null,
                originAccount.getId(),
                buildDescription(transactionDto.description(), "sem origem")
        );
    }

    private TransactionEntity processWithdraw(Integer accountId, TransactionDto transactionDto) {
        AccountEntity originAccount = getAccountById(accountId);
        validateSufficientBalance(originAccount, transactionDto.amount(), "Saldo insuficiente para saque");

        originAccount.setBalance(originAccount.getBalance() - transactionDto.amount());
        accountRepository.save(originAccount);

        return saveTransaction(
                transactionDto,
                originAccount.getId(),
                null,
                buildDescription(transactionDto.description(), "sem destino")
        );
    }

    private TransactionEntity processTransfer(Integer accountId, TransactionDto transactionDto) {
        AccountEntity originAccount = getAccountById(accountId);
        AccountEntity destinationAccount = getDestinationAccount(transactionDto.destinationAccountId());

        if (originAccount.getId().equals(destinationAccount.getId())) {
            throw new BadRequestException("A conta de destino deve ser diferente da conta de origem");
        }

        validateSufficientBalance(originAccount, transactionDto.amount(), "Saldo insuficiente para transferencia");

        originAccount.setBalance(originAccount.getBalance() - transactionDto.amount());
        destinationAccount.setBalance(destinationAccount.getBalance() + transactionDto.amount());

        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        return saveTransaction(
                transactionDto,
                originAccount.getId(),
                destinationAccount.getId(),
                transactionDto.description()
        );
    }

    private TransactionEntity saveTransaction(
            TransactionDto transactionDto,
            Integer originAccountId,
            Integer destinationAccountId,
            String description
    ) {
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .amount(transactionDto.amount())
                .typeTransaction(transactionDto.typeTransaction())
                .description(description)
                .createdAt(LocalDateTime.now())
                .originAccountId(originAccountId)
                .destinationAccountId(destinationAccountId)
                .build();

        return transactionRepositoty.save(transactionEntity);
    }

    private void validateTransactionDto(TransactionDto transactionDto) {
        if (transactionDto == null) {
            throw new BadRequestException("Transacao invalida");
        }

        if (transactionDto.typeTransaction() == null) {
            throw new BadRequestException("Tipo de transacao e obrigatorio");
        }

        if (transactionDto.amount() == null) {
            throw new BadRequestException("O valor da transacao e obrigatorio");
        }

        if (transactionDto.amount() <= 0) {
            throw new BadRequestException("O valor da transacao deve ser maior que zero");
        }

        if (transactionDto.typeTransaction() == EnumTypeTransaction.TRANSFER
                && transactionDto.destinationAccountId() == null) {
            throw new BadRequestException("O id da conta de destino e obrigatorio para transferencia");
        }
    }

    private AccountEntity getAccountById(Integer accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada: " + accountId));
    }

    private AccountEntity getDestinationAccount(Integer destinationAccountId) {
        return accountRepository.findById(destinationAccountId)
                .orElseThrow(() -> new NotFoundException("Conta de destino nao encontrada: " + destinationAccountId));
    }

    private void validateSufficientBalance(AccountEntity accountEntity, Double amount, String message) {
        if (accountEntity.getBalance() < amount) {
            throw new BadRequestException(message);
        }
    }

    private void validateAccountId(Integer accountId) {
        if (accountId == null || accountId <= 0) {
            throw new BadRequestException("O id da conta deve ser maior que zero");
        }
    }

    private String buildDescription(String description, String fallback) {
        if (description == null || description.isBlank()) {
            return fallback;
        }

        return description + " - " + fallback;
    }
}
