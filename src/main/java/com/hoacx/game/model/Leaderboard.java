package com.hoacx.game.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leaderboard")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "score")
    private int score;
}
