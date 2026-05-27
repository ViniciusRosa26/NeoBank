package com.example.NeoBank.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenProvider tokenProvider;

    public WebSocketAuthHandshakeInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler webSocketHandler,
            Map<String, Object> attributes
    ) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        String token = servletRequest.getServletRequest().getParameter("token");
        if (token == null || token.isBlank() || !tokenProvider.validateToken(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        attributes.put("username", tokenProvider.getUsernameFromToken(token));
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler webSocketHandler,
            Exception exception
    ) {
    }
}
