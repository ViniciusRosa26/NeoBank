package com.example.NeoBank.dto;

import com.example.NeoBank.enums.EnumTypeTransaction;
import com.example.NeoBank.enums.PixKeyTypeEnum;

public record TransactionDto(Double amount,
                             EnumTypeTransaction type,
                             String description,
                             Integer destinationAccountId,
                             String destinationPixKey,
                             PixKeyTypeEnum pixKeyType
) {}
