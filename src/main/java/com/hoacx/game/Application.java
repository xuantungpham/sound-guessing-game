package com.hoacx.game;

import com.hoacx.game.service.RoundManager;
import com.hoacx.game.websocket.WebSocketBroadcaster;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(RoundManager roundManager, WebSocketBroadcaster broadcaster) {
        return args -> {
            roundManager.setBroadcaster(broadcaster);
            System.out.println("WebSocket linked to RoundManager");
        };
    }
}
