package main.java.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="rooms")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="room_id")
    private int id;

    @Column(name="room_name")
    private String name;

    private String status;

    @Column(name="current_players")
    private int currentPlayers;
}
