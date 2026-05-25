package com.example.NeoBank.service;


import com.example.NeoBank.dto.UpdateEmailDto;
import com.example.NeoBank.dto.UserDto;
import com.example.NeoBank.dto.UserSummaryDto;
import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.CreditCardEntity;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.enums.Role;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserService authenticatedUserService;


    public void createUser(UserDto userDto) throws BadRequestException {

        UserEntity userEntity = userRepository.findByEmail(userDto.email()).orElse(null);

        if (userEntity != null) {

            throw new BadRequestException("Email já cadastrado");
        }
        UserEntity user = UserEntity.builder()
                .name(userDto.name())
                .email(userDto.email())
                .password(passwordEncoder.encode(userDto.password()))
                .phone(userDto.phone())
                .cpf(userDto.cpf())
                .occupationEnum(userDto.occupationEnum())
                .salary(userDto.salary())
                .typeAccountEnum(userDto.typeAccountEnum())
                .dateNasciment(userDto.dateNasciment())
                .createdAt(LocalDateTime.now())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        AccountEntity account = accountService.createDefaultAccount(user);
        CreditCardEntity creditCardEntity = creditCardService.createDefaultCreditCard(user, account);
    }

    public UserEntity getUserbyID(Integer id) throws BadRequestException {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado: " + id));
        return userEntity;
    }

    public UserSummaryDto getAuthenticatedUserSummary() {
        UserEntity userEntity = authenticatedUserService.getAuthenticatedUser();
        AccountEntity accountEntity = authenticatedUserService.getAuthenticatedAccount();
        CreditCardEntity creditCardEntity = accountEntity.getCreditCardEntity();

        return toUserSummary(userEntity, accountEntity, creditCardEntity);
    }

    public UserEntity setUserbyID(Integer id, UserDto userDto) throws BadRequestException {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado: " + id));

        userEntity.setName(userDto.name());
        userEntity.setEmail(userDto.email());
        userEntity.setPassword(passwordEncoder.encode(userDto.password()));
        userEntity.setPhone(userDto.phone());
        userEntity.setCpf(userDto.cpf());
        userEntity.setOccupationEnum(userDto.occupationEnum());
        userEntity.setSalary(userDto.salary());
        userEntity.setTypeAccountEnum(userDto.typeAccountEnum());
        userEntity.setDateNasciment(userDto.dateNasciment());


        return userRepository.save(userEntity);
    }

    public UserSummaryDto updateAuthenticatedUserEmail(UpdateEmailDto updateEmailDto) {
        if (updateEmailDto == null || updateEmailDto.email() == null || updateEmailDto.email().isBlank()) {
            throw new BadRequestException("Email e obrigatorio");
        }

        UserEntity authenticatedUser = authenticatedUserService.getAuthenticatedUser();
        UserEntity existingUser = userRepository.findByEmail(updateEmailDto.email()).orElse(null);

        if (existingUser != null && !existingUser.getId().equals(authenticatedUser.getId())) {
            throw new BadRequestException("Email ja cadastrado");
        }

        authenticatedUser.setEmail(updateEmailDto.email().trim());
        UserEntity savedUser = userRepository.save(authenticatedUser);
        AccountEntity accountEntity = authenticatedUserService.getAuthenticatedAccount();

        return toUserSummary(savedUser, accountEntity, accountEntity.getCreditCardEntity());
    }

    public void deleteUserbyID(Integer id) throws BadRequestException {

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado: " + id));


        //voltar aqui e implementar transactional
        userRepository.delete(userEntity);
    }

    private UserSummaryDto toUserSummary(
            UserEntity userEntity,
            AccountEntity accountEntity,
            CreditCardEntity creditCardEntity
    ) {
        return new UserSummaryDto(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getCpf(),
                userEntity.getPhone(),
                userEntity.getOccupationEnum(),
                userEntity.getSalary(),
                userEntity.getTypeAccountEnum(),
                userEntity.getDateNasciment(),
                accountEntity.getId(),
                accountEntity.getBalance(),
                accountEntity.getDiaryLimitPix(),
                accountEntity.getNightLimitPix(),
                creditCardEntity != null ? creditCardEntity.getLimitCredit() : null,
                creditCardEntity != null ? creditCardEntity.getHolderName() : null,
                creditCardEntity != null ? maskCardNumber(creditCardEntity.getNumber()) : null,
                creditCardEntity != null ? creditCardEntity.getExpirationDate() : null
        );
    }

    private String maskCardNumber(String number) {
        if (number == null || number.length() < 4) {
            return "Cartao indisponivel";
        }

        return "**** **** **** " + number.substring(number.length() - 4);
    }
}
