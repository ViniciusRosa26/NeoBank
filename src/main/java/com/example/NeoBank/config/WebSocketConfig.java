package com.example.NeoBank.config;

import com.example.NeoBank.websocket.BalanceWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final BalanceWebSocketHandler balanceWebSocketHandler;
    private final WebSocketAuthHandshakeInterceptor webSocketAuthHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(balanceWebSocketHandler, "/ws/balance")
                .addInterceptors(webSocketAuthHandshakeInterceptor)
                .setAllowedOrigins("http://localhost:5173");
    }
}
