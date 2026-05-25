package com.example.NeoBank.dto;

import com.example.NeoBank.enums.OccupationEnum;
import com.example.NeoBank.enums.TypeAccountEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

public record UserSummaryDto(
        Integer userId,
        String name,
        String email,
        String cpf,
        String phone,
        OccupationEnum occupation,
        Double salary,
        TypeAccountEnum accountType,
        @JsonFormat(pattern = "dd-MM-yyyy")
        Date birthDate,
        Integer accountId,
        Double balance,
        BigDecimal dailyPixLimit,
        BigDecimal nightPixLimit,
        Double creditCardLimit,
        String creditCardHolder,
        String creditCardNumber,
        String creditCardExpirationDate
) {
}
