package main.java.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="audio_files")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="audio_id")
    private int id;

    @Column(name="file_path")
    private String filePath;

    private String name;
    private String category;
}
