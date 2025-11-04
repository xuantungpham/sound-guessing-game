package com.hoacx.game.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "game_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private int id;

    @Column(name = "session_code", nullable = false, unique = true)
    private String sessionCode;

    @Column(name = "status")
    private String status; // ví dụ: "WAITING", "PLAYING", "FINISHED"

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<PlayerAnswer> playerAnswers;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;
}
