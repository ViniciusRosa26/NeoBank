package com.example.NeoBank.controller;

import com.example.NeoBank.dto.ForgotPasswordDto;
import com.example.NeoBank.dto.LoginRequestDto;
import com.example.NeoBank.dto.LoginResponseDto;
import com.example.NeoBank.dto.MessageResponseDto;
import com.example.NeoBank.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }

    @PutMapping("/forgot-password")
    public MessageResponseDto forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        return authService.forgotPassword(forgotPasswordDto);
    }
}
