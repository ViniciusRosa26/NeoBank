package com.example.NeoBank.service;

import com.example.NeoBank.dto.TransactionDto;
import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.PixKeyEntity;
import com.example.NeoBank.entity.TransactionEntity;
import com.example.NeoBank.enums.EnumTypeTransaction;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.exception.NotFoundException;
import com.example.NeoBank.repository.AccountRepository;
import com.example.NeoBank.repository.PixKeyRepository;
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
    private final PixKeyRepository pixKeyRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Transactional
    public TransactionEntity createTransaction(TransactionDto transactionDto) {
        validateTransactionDto(transactionDto);

        return switch (transactionDto.type()) {
            case DEPOSIT -> processDeposit(transactionDto);
            case WITHDRAW -> processWithdraw(transactionDto);
            case TRANSFER -> processTransfer(transactionDto);
            case PIX -> processPix(transactionDto);
        };
    }

    private TransactionEntity processDeposit(TransactionDto transactionDto) {
        AccountEntity originAccount = getAuthenticatedAccount();
        originAccount.setBalance(originAccount.getBalance() + transactionDto.amount());
        accountRepository.save(originAccount);

        return saveTransaction(
                transactionDto,
                null,
                originAccount.getId(),
                buildDescription(transactionDto.description(), "sem origem")
        );
    }

    private TransactionEntity processWithdraw(TransactionDto transactionDto) {
        AccountEntity originAccount = getAuthenticatedAccount();
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

    private TransactionEntity processTransfer(TransactionDto transactionDto) {
        AccountEntity originAccount = getAuthenticatedAccount();
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
                resolveDescription(transactionDto.description(), "transferencia")
        );
    }

    private TransactionEntity processPix(TransactionDto transactionDto) {
        AccountEntity originAccount = getAuthenticatedAccount();
        PixKeyEntity destinationPixKey = pixKeyRepository.findByKeyValue(transactionDto.destinationPixKey())
                .orElseThrow(() -> new NotFoundException("Chave Pix nao encontrada: " + transactionDto.destinationPixKey()));

        if (transactionDto.pixKeyType() != destinationPixKey.getKeyType()) {
            throw new BadRequestException("Tipo de chave Pix incorreto");
        }

        AccountEntity destinationAccount = destinationPixKey.getAccount();

        if (destinationAccount == null) {
            throw new NotFoundException("Conta vinculada a chave Pix nao encontrada");
        }

        if (originAccount.getId().equals(destinationAccount.getId())) {
            throw new BadRequestException("Nao e permitido fazer Pix para a propria conta");
        }

        validateSufficientBalance(originAccount, transactionDto.amount(), "Saldo insuficiente para pix");

        originAccount.setBalance(originAccount.getBalance() - transactionDto.amount());
        destinationAccount.setBalance(destinationAccount.getBalance() + transactionDto.amount());

        accountRepository.save(originAccount);
        accountRepository.save(destinationAccount);

        return saveTransaction(
                transactionDto,
                originAccount.getId(),
                destinationAccount.getId(),
                resolveDescription(transactionDto.description(), "pix")
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
                .typeTransaction(transactionDto.type())
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

        if (transactionDto.type() == null) {
            throw new BadRequestException("Tipo de transacao e obrigatorio");
        }

        if (transactionDto.amount() == null) {
            throw new BadRequestException("O valor da transacao e obrigatorio");
        }

        if (transactionDto.amount() <= 0) {
            throw new BadRequestException("O valor da transacao deve ser maior que zero");
        }

        if (transactionDto.type() == EnumTypeTransaction.TRANSFER
                && transactionDto.destinationAccountId() == null) {
            throw new BadRequestException("O id da conta de destino e obrigatorio para transferencia");
        }

        if (transactionDto.type() == EnumTypeTransaction.PIX) {
            if (transactionDto.destinationPixKey() == null || transactionDto.destinationPixKey().isBlank()) {
                throw new BadRequestException("A chave Pix de destino e obrigatoria");
            }

            if (transactionDto.pixKeyType() == null) {
                throw new BadRequestException("O tipo da chave Pix e obrigatorio");
            }
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

    private String buildDescription(String description, String fallback) {
        if (description == null || description.isBlank()) {
            return fallback;
        }

        return description + " - " + fallback;
    }

    private String resolveDescription(String description, String fallback) {
        if (description == null || description.isBlank()) {
            return fallback;
        }

        return description;
    }

    private AccountEntity getAuthenticatedAccount() {
        return authenticatedUserService.getAuthenticatedAccount();
    }
}
