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
public class CategoryBreakdownDto {

    private String categoryName;
    private String categoryIcon;
    private BigDecimal totalAmount;
    private double percentage;
    private long transactionCount;
}
