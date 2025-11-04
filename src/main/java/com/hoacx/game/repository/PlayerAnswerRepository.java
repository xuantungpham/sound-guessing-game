package com.hoacx.game.repository;

import com.hoacx.game.model.PlayerAnswer;
import com.hoacx.game.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerAnswerRepository extends JpaRepository<PlayerAnswer, Integer> {

    List<PlayerAnswer> findBySession(GameSession session);

}
