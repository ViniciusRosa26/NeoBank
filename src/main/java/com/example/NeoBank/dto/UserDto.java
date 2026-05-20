package com.example.NeoBank.dto;

import com.example.NeoBank.enums.OccupationEnum;
import com.example.NeoBank.enums.TypeAccountEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record UserDto(String name,
                      String email,
                      String password,
                      OccupationEnum occupationEnum,
                      String cpf ,
                      String phone,
                      Double salary,
                      TypeAccountEnum typeAccountEnum,
                      @JsonFormat(pattern = "dd-MM-yyyy")
                      Date dateNasciment
) {
}
