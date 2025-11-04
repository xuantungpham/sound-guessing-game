package com.hoacx.game.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketBroadcaster {

    // Lưu tất cả session của người chơi theo sessionCode
    private final Map<String, Map<String, WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public void registerSession(String sessionCode, WebSocketSession session) {
        sessions.computeIfAbsent(sessionCode, k -> new ConcurrentHashMap<>())
                .put(session.getId(), session);
        System.out.println("Registered session " + session.getId() + " in " + sessionCode);
    }

    public void removeSession(String sessionCode, String sessionId) {
        if (sessions.containsKey(sessionCode)) {
            sessions.get(sessionCode).remove(sessionId);
            System.out.println("Removed session " + sessionId + " from " + sessionCode);
        }
    }

    public void broadcastToSession(String sessionCode, String message) {
        if (!sessions.containsKey(sessionCode)) return;
        for (WebSocketSession s : sessions.get(sessionCode).values()) {
            try {
                s.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                System.err.println("Failed to send message to " + s.getId() + ": " + e.getMessage());
            }
        }
    }
}
