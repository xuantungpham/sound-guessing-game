package com.hoacx.game.service;

import com.hoacx.game.websocket.WebSocketBroadcaster;
import com.hoacx.game.model.GameSession;
import org.springframework.stereotype.Service;

@Service
public class RoundManager {

    private WebSocketBroadcaster broadcaster;

    public void setBroadcaster(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }


    public void startRound(GameSession session) {
        System.out.println("Starting new round for session: " + session.getSessionCode());
        if (broadcaster != null) {
            broadcaster.broadcastToSession(session.getSessionCode(), "New round started!");
        }
    }


    public void endRound(GameSession session) {
        System.out.println("Ending round for session: " + session.getSessionCode());
        if (broadcaster != null) {
            broadcaster.broadcastToSession(session.getSessionCode(), "Round ended!");
        }
    }


    public void sendScores(GameSession session) {
        if (broadcaster != null) {
            broadcaster.broadcastToSession(session.getSessionCode(), "Scores updated!");
        }
    }
}
