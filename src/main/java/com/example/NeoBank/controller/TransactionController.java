package com.example.NeoBank.controller;

import com.example.NeoBank.dto.TransactionDto;
import com.example.NeoBank.entity.TransactionEntity;
import com.example.NeoBank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionEntity createTransaction(@PathVariable Integer id, @RequestBody TransactionDto transactionDto) {
        return transactionService.createTransaction(id, transactionDto);
    }
}
