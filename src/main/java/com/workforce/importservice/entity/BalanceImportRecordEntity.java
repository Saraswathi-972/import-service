package com.workforce.importservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "balance_import_records")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceImportRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String aaid;
    @Column(nullable = false)
    private String balanceName;
    @Column(nullable = false)
    private BigDecimal balanceValue;
    @Column(nullable = false)
    private String action;
    @Column(nullable = false)
    private LocalDate effectiveDate;
    private String comments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_job_id", nullable = false)
    private ImportJob importJob;
    @Enumerated(EnumType.STRING)
    @Column(name = "import_status", nullable = false)
    private ImportRecordStatus importRecordStatus;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
