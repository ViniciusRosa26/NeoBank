package com.example.NeoBank.websocket;

import com.example.NeoBank.service.BalanceWebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class BalanceWebSocketHandler extends TextWebSocketHandler {

    private final BalanceWebSocketService balanceWebSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");

        if (username == null || username.isBlank()) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Usuario nao autenticado"));
            return;
        }

        balanceWebSocketService.registerSession(username, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // o cliente so recebe eventos; nao processamos mensagens recebidas nesta fase
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = (String) session.getAttributes().get("username");
        balanceWebSocketService.unregisterSession(username, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String username = (String) session.getAttributes().get("username");
        balanceWebSocketService.unregisterSession(username, session);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
}
