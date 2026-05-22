package com.example.NeoBank.dto;

import com.example.NeoBank.enums.PixKeyTypeEnum;

public record PixKeyDto(String keyValue,
                        PixKeyTypeEnum keyType,
                        Integer accountId) {
}
