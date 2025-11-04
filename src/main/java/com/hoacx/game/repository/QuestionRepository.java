package com.hoacx.game.repository;

import com.hoacx.game.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    // Bạn có thể thêm phương thức truy vấn tùy chọn ở đây nếu cần
}
