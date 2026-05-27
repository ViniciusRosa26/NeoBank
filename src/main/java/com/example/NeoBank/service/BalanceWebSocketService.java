package com.example.NeoBank.service;

import com.example.NeoBank.dto.BalanceUpdateDto;
import com.example.NeoBank.entity.AccountEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@RequiredArgsConstructor
public class BalanceWebSocketService {

    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, CopyOnWriteArraySet<WebSocketSession>> sessionsByUsername = new ConcurrentHashMap<>();

    public void registerSession(String username, WebSocketSession session) {
        sessionsByUsername.computeIfAbsent(username, ignored -> new CopyOnWriteArraySet<>()).add(session);
    }

    public void unregisterSession(String username, WebSocketSession session) {
        if (username == null) {
            return;
        }

        Set<WebSocketSession> sessions = sessionsByUsername.get(username);
        if (sessions == null) {
            return;
        }

        sessions.remove(session);
        if (sessions.isEmpty()) {
            sessionsByUsername.remove(username);
        }
    }

    public void sendBalanceUpdate(AccountEntity accountEntity, BalanceUpdateDto payload) {
        if (accountEntity.getUser() == null || accountEntity.getUser().getEmail() == null) {
            return;
        }

        String username = accountEntity.getUser().getEmail();
        Set<WebSocketSession> sessions = sessionsByUsername.get(username);

        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        String message = toJson(payload);

        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                unregisterSession(username, session);
                continue;
            }

            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException ignored) {
                unregisterSession(username, session);
            }
        }
    }

    private String toJson(BalanceUpdateDto payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Falha ao serializar atualizacao de saldo", e);
        }
    }
}
