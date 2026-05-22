package com.example.NeoBank.controller;

import com.example.NeoBank.dto.BalanceResponseDto;
import com.example.NeoBank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me/balance")
    public BalanceResponseDto getMyBalance() {
        return accountService.getAuthenticatedBalance();
    }
}
