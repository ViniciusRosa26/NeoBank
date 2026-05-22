package com.example.NeoBank.dto;

public record ForgotPasswordDto(String email,
                                String cpf,
                                String phone,
                                String newPassword) {
}
