package com.workforce.importservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeBalanceRequest {
    private String employeeNumber;
    private String balanceName;
    private BigDecimal balanceValue;
    private String action;
}
