package main.java.service;

import main.java.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RoomManager {

    private final RoomRepository roomRepository;
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, Integer> sessionToUser = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();

    public synchronized void joinRoom(String roomId, String userId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionToUser.put(session.getId(), Integer.parseInt(userId));
        sessionToRoom.put(session.getId(), roomId);

        roomRepository.findById(Integer.parseInt(roomId)).ifPresent(r -> {
            r.setCurrentPlayers(r.getCurrentPlayers() + 1);
            roomRepository.save(r);
        });
    }

    public synchronized void leaveRoom(WebSocketSession session) {
        String roomId = sessionToRoom.get(session.getId());
        if (roomId == null) return;

        roomSessions.getOrDefault(roomId, Collections.emptySet()).remove(session);
        sessionToUser.remove(session.getId());
        sessionToRoom.remove(session.getId());

        roomRepository.findById(Integer.parseInt(roomId)).ifPresent(r -> {
            r.setCurrentPlayers(Math.max(0, r.getCurrentPlayers() - 1));
            roomRepository.save(r);
        });
    }

    public List<WebSocketSession> getSessions(String roomId) {
        return new ArrayList<>(roomSessions.getOrDefault(roomId, Collections.emptySet()));
    }

    public Integer getUserId(String sessionId) {
        return sessionToUser.get(sessionId);
    }

    public String getRoomId(String sessionId) {
        return sessionToRoom.get(sessionId);
    }
}
