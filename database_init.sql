USE sound_guess_game;

INSERT INTO users(username,password,email,score_total)
VALUES ('hoa','123','hoa@gmail.com',0),
       ('minh','123','minh@gmail.com',0),
       ('lan','123','lan@gmail.com',0),
       ('son','123','son@gmail.com',0),
       ('trang','123','trang@gmail.com',0);

INSERT INTO rooms(room_name,status,current_players)
VALUES ('Room1','WAITING',0);

INSERT INTO audio_files(file_path,name,category)
VALUES ('static/sound/sound1.mp3','sound1','animal'),
       ('static/sound/sound2.mp3','sound2','vehicle'),
       ('static/sound/sound3.mp3','sound3','music'),
       ('static/sound/sound4.mp3','sound4','nature'),
       ('static/sound/sound5.mp3','sound5','other');

INSERT INTO questions(audio_id,option_a,option_b,option_c,option_d,correct_option)
VALUES (1,'Cat','Dog','Tiger','Cow','B'),
       (2,'Car','Train','Plane','Ship','A'),
       (3,'Piano','Drum','Guitar','Violin','C'),
       (4,'Rain','Fire','Wind','Thunder','A'),
       (5,'Lion','Snake','Elephant','Wolf','D');
