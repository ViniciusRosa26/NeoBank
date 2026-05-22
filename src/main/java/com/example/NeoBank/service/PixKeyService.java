package com.example.NeoBank.service;

import com.example.NeoBank.dto.PixKeyDto;
import com.example.NeoBank.entity.AccountEntity;
import com.example.NeoBank.entity.PixKeyEntity;
import com.example.NeoBank.exception.BadRequestException;
import com.example.NeoBank.exception.NotFoundException;
import com.example.NeoBank.repository.AccountRepository;
import com.example.NeoBank.repository.PixKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PixKeyService {

    private final PixKeyRepository pixKeyRepository;
    private final AccountRepository accountRepository;

    public PixKeyEntity createPixKey(PixKeyDto pixKeyDto) {
        validatePixKeyDto(pixKeyDto);

        AccountEntity accountEntity = accountRepository.findById(pixKeyDto.accountId())
                .orElseThrow(() -> new NotFoundException("Conta nao encontrada: " + pixKeyDto.accountId()));

        if (pixKeyRepository.findByKeyValue(pixKeyDto.keyValue()).isPresent()) {
            throw new BadRequestException("Chave Pix ja cadastrada");
        }

        PixKeyEntity pixKeyEntity = PixKeyEntity.builder()
                .keyValue(pixKeyDto.keyValue())
                .keyType(pixKeyDto.keyType())
                .account(accountEntity)
                .build();

        return pixKeyRepository.save(pixKeyEntity);
    }

    private void validatePixKeyDto(PixKeyDto pixKeyDto) {
        if (pixKeyDto == null) {
            throw new BadRequestException("Dados da chave Pix sao obrigatorios");
        }

        if (pixKeyDto.accountId() == null || pixKeyDto.accountId() <= 0) {
            throw new BadRequestException("O id da conta deve ser maior que zero");
        }

        if (pixKeyDto.keyType() == null) {
            throw new BadRequestException("O tipo da chave Pix e obrigatorio");
        }

        if (pixKeyDto.keyValue() == null || pixKeyDto.keyValue().isBlank()) {
            throw new BadRequestException("O valor da chave Pix e obrigatorio");
        }
    }
}
