package com.example.NeoBank.dto;

import com.example.NeoBank.enums.EnumTypeTransaction;

public record TransactionDto(Double amount,
                             EnumTypeTransaction typeTransaction,
                             String description,
                            Integer destinationAccountId
                             ) {}
