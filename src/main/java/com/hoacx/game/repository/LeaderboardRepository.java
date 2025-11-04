package com.hoacx.game.repository;

import com.hoacx.game.model.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Integer> {
    // Có thể thêm custom query sau nếu cần, ví dụ:
    // List<Leaderboard> findTop10ByOrderByScoreDesc();
}
