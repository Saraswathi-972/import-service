package com.workforce.importservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailedRecordResponse {

    private String aaid;
    private String balanceName;
    private String action;
    private BigDecimal balanceValue;
    private LocalDate effectiveDate;
    private String comments;

    private String status;
    private String errorMessage;
}