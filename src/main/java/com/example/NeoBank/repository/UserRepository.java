package com.example.NeoBank.repository;

import com.example.NeoBank.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {


    Optional<UserEntity> findByEmail(String email);

    String email(String email);
}
