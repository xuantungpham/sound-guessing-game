package com.hoacx.game.repository;

import com.hoacx.game.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Integer> {
    // Có thể thêm phương thức tìm kiếm tùy chọn ở đây nếu cần
}
