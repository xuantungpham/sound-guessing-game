CREATE DATABASE IF NOT EXISTS sound_guess_game;
USE sound_guess_game;

-- 1. Bảng người chơi
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng phòng chơi
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    status ENUM('waiting', 'playing') DEFAULT 'waiting',
    current_players INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Bảng âm thanh
CREATE TABLE audios (
    audio_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category ENUM('animal', 'instrument', 'vehicle', 'other'),
    file_path VARCHAR(255) NOT NULL
);

-- 4. Bảng câu hỏi
CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    audio_id INT NOT NULL,
    option_a VARCHAR(100) NOT NULL,
    option_b VARCHAR(100) NOT NULL,
    option_c VARCHAR(100) NOT NULL,
    option_d VARCHAR(100) NOT NULL,
    correct_option CHAR(1) NOT NULL,
    FOREIGN KEY (audio_id) REFERENCES audios(audio_id)
);

-- 5. Bảng phiên chơi (game session)
CREATE TABLE game_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_time DATETIME,
    total_questions INT DEFAULT 10,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

-- 6. Bảng câu trả lời của người chơi
CREATE TABLE player_answers (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    question_id INT NOT NULL,
    user_id INT NOT NULL,
    selected_option CHAR(1),
    is_correct BOOLEAN,
    answer_time FLOAT, -- thời gian trả lời (giây)
    FOREIGN KEY (session_id) REFERENCES game_sessions(session_id),
    FOREIGN KEY (question_id) REFERENCES questions(question_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 7. Bảng xếp hạng
CREATE TABLE leaderboards (
    leaderboard_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    user_id INT NOT NULL,
    total_points FLOAT DEFAULT 0,
    correct_count INT DEFAULT 0,
    FOREIGN KEY (session_id) REFERENCES game_sessions(session_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Người chơi
INSERT INTO users (username, password, full_name) VALUES
('alice', '123', 'Alice Nguyen'),
('bob', '123', 'Bob Tran');

-- Âm thanh
INSERT INTO audios (name, category, file_path) VALUES
('Dog Barking', 'animal', 'dog_bark.mp3'),
('Guitar', 'instrument', 'guitar.mp3'),
('Train Horn', 'vehicle', 'train_horn.mp3');

-- Câu hỏi
INSERT INTO questions (audio_id, option_a, option_b, option_c, option_d, correct_option) VALUES
(1, 'Dog Barking', 'Cat Meowing', 'Cow Mooing', 'Car Horn', 'A'),
(2, 'Drum', 'Guitar', 'Piano', 'Violin', 'B'),
(3, 'Car', 'Helicopter', 'Train', 'Boat', 'C');


