package com.example.NeoBank.service;

import com.example.NeoBank.config.TokenProvider;
import com.example.NeoBank.dto.ForgotPasswordDto;
import com.example.NeoBank.dto.LoginRequestDto;
import com.example.NeoBank.dto.LoginResponseDto;
import com.example.NeoBank.dto.MessageResponseDto;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.enums.Role;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.exception.NotFoundException;
import com.example.NeoBank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        validateLoginRequest(loginRequestDto);

        UserEntity userEntity = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() -> new BadRequestException("Email ou senha invalidos"));

        if (!passwordEncoder.matches(loginRequestDto.password(), userEntity.getPassword())) {
            throw new BadRequestException("Email ou senha invalidos");
        }

        Role role = requireRole(userEntity);
        String token = tokenProvider.generateToken(userEntity);

        return new LoginResponseDto(
                token,
                userEntity.getId(),
                userEntity.getEmail(),
                role.name()
        );
    }

    public MessageResponseDto forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        validateForgotPasswordRequest(forgotPasswordDto);

        UserEntity userEntity = userRepository.findByEmail(forgotPasswordDto.email())
                .orElseThrow(() -> new NotFoundException("Usuario nao encontrado com email: " + forgotPasswordDto.email()));

        if (!userEntity.getCpf().equals(forgotPasswordDto.cpf())
                || !userEntity.getPhone().equals(forgotPasswordDto.phone())) {
            throw new BadRequestException("Os dados informados nao conferem");
        }

        userEntity.setPassword(passwordEncoder.encode(forgotPasswordDto.newPassword()));
        userRepository.save(userEntity);

        return new MessageResponseDto("Senha atualizada com sucesso");
    }

    private void validateLoginRequest(LoginRequestDto loginRequestDto) {
        if (loginRequestDto == null) {
            throw new BadRequestException("Dados de login sao obrigatorios");
        }

        if (loginRequestDto.email() == null || loginRequestDto.email().isBlank()) {
            throw new BadRequestException("Email e obrigatorio");
        }

        if (loginRequestDto.password() == null || loginRequestDto.password().isBlank()) {
            throw new BadRequestException("Senha e obrigatoria");
        }
    }

    private void validateForgotPasswordRequest(ForgotPasswordDto forgotPasswordDto) {
        if (forgotPasswordDto == null) {
            throw new BadRequestException("Dados de recuperacao sao obrigatorios");
        }

        if (forgotPasswordDto.email() == null || forgotPasswordDto.email().isBlank()) {
            throw new BadRequestException("Email e obrigatorio");
        }

        if (forgotPasswordDto.cpf() == null || forgotPasswordDto.cpf().isBlank()) {
            throw new BadRequestException("CPF e obrigatorio");
        }

        if (forgotPasswordDto.phone() == null || forgotPasswordDto.phone().isBlank()) {
            throw new BadRequestException("Telefone e obrigatorio");
        }

        if (forgotPasswordDto.newPassword() == null || forgotPasswordDto.newPassword().isBlank()) {
            throw new BadRequestException("Nova senha e obrigatoria");
        }
    }

    private Role requireRole(UserEntity userEntity) {
        if (userEntity.getRole() == null) {
            throw new BadRequestException("Usuario sem perfil configurado. Atualize a role antes de realizar login.");
        }

        return userEntity.getRole();
    }
}
