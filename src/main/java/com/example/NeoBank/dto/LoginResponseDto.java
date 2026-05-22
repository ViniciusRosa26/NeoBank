package com.example.NeoBank.dto;

public record LoginResponseDto(String token,
                               Integer userId,
                               String email,
                               String role) {
}
