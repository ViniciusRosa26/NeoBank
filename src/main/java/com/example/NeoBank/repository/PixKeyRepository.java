package com.example.NeoBank.repository;

import com.example.NeoBank.entity.PixKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PixKeyRepository extends JpaRepository<PixKeyEntity, Integer> {

    Optional<PixKeyEntity> findByKeyValue(String keyValue);
}
