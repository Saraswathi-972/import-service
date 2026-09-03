package com.workforce.importservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceImportRecord {

    private String aaid;
    private String balanceName;
    private String action;
    private BigDecimal balanceValue;
    private LocalDate effectiveDate;
    private String comments;
}
