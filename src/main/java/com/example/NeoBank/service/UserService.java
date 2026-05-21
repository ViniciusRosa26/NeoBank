package com.example.NeoBank.service;


import com.example.NeoBank.dto.UserDto;
import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.CreditCardEntity;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.enums.Role;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final CreditCardService creditCardService;


    public void createUser(UserDto userDto) throws BadRequestException {

        UserEntity userEntity = userRepository.findByEmail(userDto.email()).orElse(null);

        if (userEntity != null) {

            throw new BadRequestException("Email já cadastrado");
        }
        UserEntity user = UserEntity.builder()
                .name(userDto.name())
                .email(userDto.email())
                .password(userDto.password())
                .phone(userDto.phone())
                .cpf(userDto.cpf())
                .occupationEnum(userDto.occupationEnum())
                .salary(userDto.salary())
                .typeAccountEnum(userDto.typeAccountEnum())
                .dateNasciment(userDto.dateNasciment())
                .createdAt(LocalDateTime.now())
                .role(Role.valueOf("USER"))
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

    public UserEntity setUserbyID(Integer id, UserDto userDto) throws BadRequestException {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado: " + id));

        userEntity.setName(userDto.name());
        userEntity.setEmail(userDto.email());
        userEntity.setPassword(userDto.password());
        userEntity.setPhone(userDto.phone());
        userEntity.setCpf(userDto.cpf());
        userEntity.setOccupationEnum(userDto.occupationEnum());
        userEntity.setSalary(userDto.salary());
        userEntity.setTypeAccountEnum(userDto.typeAccountEnum());
        userEntity.setDateNasciment(userDto.dateNasciment());


        return userRepository.save(userEntity);
    }

    public void deleteUserbyID(Integer id) throws BadRequestException {

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Usuário não encontrado: " + id));


        //voltar aqui e implementar transactional
        userRepository.delete(userEntity);
    }
}
