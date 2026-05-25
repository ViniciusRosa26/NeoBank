package com.example.NeoBank.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseConstraintInitializer {

    private final JdbcTemplate jdbcTemplate;

    @Bean
    ApplicationRunner syncTransactionTypeConstraint() {
        return args -> {
            jdbcTemplate.execute("""
                    ALTER TABLE transactions
                    DROP CONSTRAINT IF EXISTS transactions_type_transaction_check
                    """);

            jdbcTemplate.execute("""
                    ALTER TABLE transactions
                    ADD CONSTRAINT transactions_type_transaction_check
                    CHECK (type_transaction IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER', 'PIX'))
                    """);

            log.info("Constraint transactions_type_transaction_check sincronizada com os tipos de transacao atuais");
        };
    }
}
