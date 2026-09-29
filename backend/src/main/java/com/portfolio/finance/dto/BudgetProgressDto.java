package com.portfolio.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetProgressDto {

    private Long id;
    private String name;
    private BigDecimal limitAmount;
    private Long categoryId;
    private String categoryName;
    private int month;
    private int year;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double percentUsed;
}
