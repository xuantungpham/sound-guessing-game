CREATE DATABASE IF NOT EXISTS audio_guess_game;
USE audio_guess_game;

-- TẠO BẢNG

-- bảng USERS
CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(100),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- AUDIO FILES (lưu path chứ ko lưu blob theo khuyến nghị)
CREATE TABLE audio_files (
  audio_id INT AUTO_INCREMENT PRIMARY KEY,
  filename VARCHAR(255) NOT NULL,    -- tên file trên server
  filepath VARCHAR(1024) NOT NULL,   -- đường dẫn tuyệt đối/relative
  category VARCHAR(50),              -- 'animal', 'instrument', 'vehicle', ...
  duration_ms INT,                   -- độ dài thực tế
  md5_hash VARCHAR(64),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- bảng ROOMS
CREATE TABLE rooms (
  room_id INT AUTO_INCREMENT PRIMARY KEY,
  room_name VARCHAR(100),
  max_players INT DEFAULT 20,
  status ENUM('idle','playing') DEFAULT 'idle',
  created_by INT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- MAPPING USERS IN ROOM có bảng sau
CREATE TABLE room_players (
  room_id INT,
  user_id INT,
  joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  is_host BOOLEAN DEFAULT FALSE,
  PRIMARY KEY(room_id, user_id),
  FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- GAMES (1 ván trong 1 room)
CREATE TABLE games (
  game_id INT AUTO_INCREMENT PRIMARY KEY,
  room_id INT NOT NULL,
  started_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  finished_at DATETIME NULL,
  total_rounds INT,
  status ENUM('ongoing','finished') DEFAULT 'ongoing',
  FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

-- QUESTIONS (tham khảo audio)
CREATE TABLE questions (
  question_id INT AUTO_INCREMENT PRIMARY KEY,
  audio_id INT NOT NULL,
  option_a VARCHAR(100) NOT NULL,
  option_b VARCHAR(100) NOT NULL,
  option_c VARCHAR(100) NOT NULL,
  option_d VARCHAR(100) NOT NULL,
  correct_option TINYINT NOT NULL, -- 1..4
  text_prompt VARCHAR(255),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (audio_id) REFERENCES audio_files(audio_id)
);

-- GAME ROUNDS (mỗi round là 1 câu trong 1 game)
CREATE TABLE game_rounds (
  round_id INT AUTO_INCREMENT PRIMARY KEY,
  game_id INT NOT NULL,
  question_id INT NOT NULL,
  round_index INT NOT NULL, -- 1..N
  started_at DATETIME,
  ended_at DATETIME,
  FOREIGN KEY (game_id) REFERENCES games(game_id) ON DELETE CASCADE,
  FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

-- ANSWERS (mỗi người 1 record cho mỗi round)
CREATE TABLE answers (
  answer_id INT AUTO_INCREMENT PRIMARY KEY,
  round_id INT NOT NULL,
  user_id INT NOT NULL,
  selected_option TINYINT NULL, -- 1..4, NULL nếu timeout
  answered_at DATETIME NULL,
  is_correct BOOLEAN,
  time_ms INT NULL, -- thời gian trả lời tính từ start của round
  score_delta DECIMAL(4,2) DEFAULT 0, -- +1 or 0 or +1.5 etc
  FOREIGN KEY (round_id) REFERENCES game_rounds(round_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  UNIQUE KEY ux_round_user (round_id, user_id)
);

-- AGGREGATED GAME RESULTS (tùy chọn, nhanh cho leaderboard)
CREATE TABLE game_results (
  result_id INT AUTO_INCREMENT PRIMARY KEY,
  game_id INT NOT NULL,
  user_id INT NOT NULL,
  total_correct INT DEFAULT 0,
  total_score DECIMAL(6,2) DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (game_id) REFERENCES games(game_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  UNIQUE KEY ux_game_user (game_id, user_id)
);

-- TẠO INDEX CHO CÁC CỘT QUERY THƯỜNG XUYÊN
CREATE INDEX idx_game_results_score ON game_results(game_id, total_score DESC);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_audio_category ON audio_files(category);

-- LƯU HOẶC CẬP NHẬT KẾT QUẢ CHƠI GAME CỦA 1 NGƯỜI CHƠI VÀO BẢNG GAME_RESULTS
DELIMITER //
CREATE PROCEDURE upsert_game_result(
  IN p_game_id INT,
  IN p_user_id INT,
  IN p_correct INT,
  IN p_score DECIMAL(6,2)
)
BEGIN
  INSERT INTO game_results (game_id, user_id, total_correct, total_score)
  VALUES (p_game_id, p_user_id, p_correct, p_score)
  ON DUPLICATE KEY UPDATE
    total_correct = total_correct + p_correct,
    total_score = total_score + p_score;
END //
DELIMITER ;


-- QUY TRÌNH CHẤM ĐIỂM
START TRANSACTION;

-- đánh dấu đúng/sai và tính score cơ bản
UPDATE answers a
JOIN game_rounds r ON a.round_id = r.round_id
JOIN questions q ON r.question_id = q.question_id
SET a.is_correct = (a.selected_option = q.correct_option),
    a.score_delta = CASE WHEN a.selected_option = q.correct_option THEN 1.0 ELSE 0 END
WHERE a.round_id = ?; -- round_id hiện tại

-- tìm thời gian nhanh nhất trong những câu đúng
SELECT MIN(time_ms) AS min_time
FROM answers
WHERE round_id = ? AND is_correct = 1;

-- giả sử min_time = X (nếu NULL thì không ai đúng)
-- gán bonus
UPDATE answers
SET score_delta = score_delta + 0.5
WHERE round_id = ? AND is_correct = 1 AND time_ms = X;

-- cập nhật bảng game_results
-- for each user in the room you can upsert:
INSERT INTO game_results (game_id, user_id, total_correct, total_score)
SELECT g.game_id, a.user_id,
       SUM(CASE WHEN a.is_correct THEN 1 ELSE 0 END) AS total_correct,
       SUM(a.score_delta) AS total_score
FROM answers a
JOIN game_rounds gr ON a.round_id = gr.round_id
JOIN games g ON gr.game_id = g.game_id
WHERE g.game_id = ?
GROUP BY a.user_id
ON DUPLICATE KEY UPDATE
  total_correct = VALUES(total_correct),
  total_score = VALUES(total_score);

COMMIT;

-- LEADERBOARD TỔNG CỘNG TRONG ROOM
SELECT u.user_id, u.username,
       SUM(gr.total_score) AS score_sum,
       SUM(gr.total_correct) AS correct_sum
FROM game_results gr
JOIN games g ON gr.game_id = g.game_id
JOIN users u ON gr.user_id = u.user_id
WHERE g.room_id = ?
GROUP BY u.user_id
ORDER BY score_sum DESC, correct_sum DESC;

-- KHO USERS
INSERT INTO users (username, password_hash, display_name) VALUES
('player1', 'hash1', 'An'),
('player2', 'hash2', 'Binh');

-- KHO AUDIOS
INSERT INTO audio_files (filename, filepath, category, duration_ms, md5_hash)
VALUES ('dog_bark.mp3', 'dog_bark.mp3', 'animal', 3500, 'abc123...');

-- KHO QUESTIONS
INSERT INTO questions (audio_id, option_a, option_b, option_c, option_d, correct_option, text_prompt)
VALUES (1, 'Dog Barking', 'Cat Meowing', 'Cow Mooing', 'Car Horn', 'A', 'What sound is this?');




