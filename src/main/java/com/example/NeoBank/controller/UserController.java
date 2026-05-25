package com.example.NeoBank.controller;


import com.example.NeoBank.dto.UserDto;
import com.example.NeoBank.dto.UserSummaryDto;
import com.example.NeoBank.dto.UpdateEmailDto;
import com.example.NeoBank.entity.UserEntity;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Validated


public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser (@RequestBody @Valid UserDto userDto) throws BadRequestException {
        userService.createUser(userDto);

    }

    @GetMapping("/{id}")
    public UserEntity getUserbyID (@PathVariable Integer id) throws BadRequestException {
        return userService.getUserbyID(id);
    }

    @GetMapping("/me")
    public UserSummaryDto getAuthenticatedUserSummary() {
        return userService.getAuthenticatedUserSummary();
    }

    @PutMapping("/{id}")
    public UserEntity updateUser (@RequestBody @Valid UserDto userDto, @PathVariable Integer id) throws BadRequestException {

        return userService.setUserbyID(id,userDto);
    }

    @PutMapping("/me/email")
    public UserSummaryDto updateAuthenticatedUserEmail(@RequestBody UpdateEmailDto updateEmailDto) {
        return userService.updateAuthenticatedUserEmail(updateEmailDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) throws BadRequestException {

        userService.deleteUserbyID(id);
    }
}
