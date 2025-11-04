package com.hoacx.game.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "player_answers")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class PlayerAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="answer_id")
    private int id;

    @ManyToOne
    @JoinColumn(name="session_id")
    private GameSession session;

    @ManyToOne
    @JoinColumn(name="question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="selected_option")
    private char selectedOption;

    @Column(name="is_correct")
    private boolean isCorrect;

    @Column(name="answer_time")
    private float answerTime;
}
