package com.example.NeoBank.service;

import com.example.NeoBank.exception.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TransactionRateLimitService {

    private static final long MAX_REQUESTS_PER_MINUTE = 10;
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofMinutes(1);

    private final StringRedisTemplate stringRedisTemplate;

    public void checkLimit(Integer accountId) {
        String key = "rate_limit:transactions:account:" + accountId;
        Long currentCount = stringRedisTemplate.opsForValue().increment(key);

        if (currentCount == null) {
            throw new IllegalStateException("Falha ao validar limite de requisicoes");
        }

        if (currentCount == 1L) {
            stringRedisTemplate.expire(key, RATE_LIMIT_WINDOW);
        }

        if (currentCount > MAX_REQUESTS_PER_MINUTE) {
            throw new TooManyRequestsException("Limite de 10 requisicoes por minuto excedido para transacoes");
        }
    }
}
