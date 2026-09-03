package com.workforce.importservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_jobs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String s3Key;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status;
    private Integer totalRecords;
    private Integer failedRecords;
    private Integer successfulRecords;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;


}
