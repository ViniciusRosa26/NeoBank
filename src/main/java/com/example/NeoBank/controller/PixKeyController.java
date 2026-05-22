package com.example.NeoBank.controller;

import com.example.NeoBank.dto.PixKeyDto;
import com.example.NeoBank.entity.PixKeyEntity;
import com.example.NeoBank.service.PixKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/pix-keys")
@RequiredArgsConstructor
public class PixKeyController {

    private final PixKeyService pixKeyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PixKeyEntity createPixKey(@RequestBody PixKeyDto pixKeyDto) {
        return pixKeyService.createPixKey(pixKeyDto);
    }
}
