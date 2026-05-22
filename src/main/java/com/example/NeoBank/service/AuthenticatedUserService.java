package com.example.NeoBank.service;

import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.exception.NotFoundException;
import com.example.NeoBank.repository.AccountRepository;
import com.example.NeoBank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public UserEntity getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new NotFoundException("Usuario autenticado nao encontrado");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Usuario autenticado nao encontrado"));
    }

    public AccountEntity getAuthenticatedAccount() {
        UserEntity userEntity = getAuthenticatedUser();

        return accountRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada para o usuario autenticado"));
    }
}
