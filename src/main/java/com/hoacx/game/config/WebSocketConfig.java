package com.hoacx.game.config;

import com.hoacx.game.controller.GameWebSocketHandler;  // ✅ dòng này cực kỳ quan trọng
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GameWebSocketHandler gameHandler;

    @Autowired
    public WebSocketConfig(GameWebSocketHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler, "/ws/game")
                .setAllowedOrigins("*");
    }
}
