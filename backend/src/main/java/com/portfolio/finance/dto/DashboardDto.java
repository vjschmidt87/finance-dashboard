package com.portfolio.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {

    private BigDecimal totalBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private double savingsRate;
    private List<TransactionDto> recentTransactions;
    private List<CategoryBreakdownDto> categoryBreakdown;
    private List<MonthlySummaryDto> monthlyTrend;
    private List<BudgetProgressDto> budgetProgress;
}
