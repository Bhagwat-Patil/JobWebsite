package com.jobwebsite.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType type; // JOB, INTERNSHIP, etc.

    @Lob
    private String content;

    private Long adminId;

    private LocalDateTime createdAt;

    private boolean approved;

}
