package com.hoacx.game.service;

import com.hoacx.game.model.*;
import com.hoacx.game.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hoacx.game.repository.PlayerAnswerRepository;
import com.hoacx.game.repository.UserRepository;
import com.hoacx.game.model.GameSession;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreCalculator {

    private final PlayerAnswerRepository playerAnswerRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;

    @Transactional
    public void calculateAndSave(GameSession session) {
        List<PlayerAnswer> answers = playerAnswerRepository.findBySession(session);
        if (answers.isEmpty()) return;

        Optional<PlayerAnswer> fastest = answers.stream()
                .filter(PlayerAnswer::isCorrect)
                .min(Comparator.comparingDouble(PlayerAnswer::getAnswerTime));

        Integer fastestUserId = fastest.map(a -> a.getUser().getId()).orElse(null);

        Map<Integer, Float> scores = new HashMap<>();
        Map<Integer, Integer> correctCount = new HashMap<>();

        for (PlayerAnswer pa : answers) {
            int uid = pa.getUser().getId();
            float add = pa.isCorrect() ? 1.0f : 0f;

            if (fastestUserId != null && uid == fastestUserId)
                add += 0.5f;

            scores.merge(uid, add, Float::sum);
            correctCount.merge(uid, pa.isCorrect()?1:0, Integer::sum);
        }

        for (var entry : scores.entrySet()) {
            User user = userRepository.findById(entry.getKey()).orElse(null);
            if (user == null) continue;


            leaderboardRepository.save(Leaderboard.builder()
                    .username(user.getUsername())
                    .score(Math.round(entry.getValue()))  // ép float -> int
                    .build());
        }

    }
}
