package com.portfolio.finance.service;

import com.portfolio.finance.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private static final int MONTHLY_TREND_MONTHS = 6;
    private static final int RECENT_TRANSACTIONS_LIMIT = 10;

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;

    @Transactional(readOnly = true)
    public DashboardDto getDashboard(Long userId, Integer month, Integer year) {
        LocalDate now = LocalDate.now();
        int currentMonth = month != null ? month : now.getMonthValue();
        int currentYear = year != null ? year : now.getYear();

        log.debug("Loading dashboard for user {} (period: {}/{})", userId, currentMonth, currentYear);

        BigDecimal totalBalance = accountService.getTotalBalance(userId);

        MonthlySummaryDto currentMonthSummary = transactionService.getMonthlySummary(userId, currentMonth, currentYear);
        BigDecimal monthlyIncome = currentMonthSummary.getTotalIncome();
        BigDecimal monthlyExpenses = currentMonthSummary.getTotalExpenses();

        double savingsRate = monthlyIncome.compareTo(BigDecimal.ZERO) > 0
                ? monthlyIncome.subtract(monthlyExpenses)
                    .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        List<TransactionDto> recentTransactions = transactionService.getRecentTransactions(
                userId, RECENT_TRANSACTIONS_LIMIT);

        LocalDate monthStart = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        List<CategoryBreakdownDto> categoryBreakdown = transactionService.getCategoryBreakdown(
                userId, monthStart, monthEnd);

        List<MonthlySummaryDto> monthlyTrend = buildMonthlyTrend(
                userId, currentMonth, currentYear, MONTHLY_TREND_MONTHS);

        List<BudgetProgressDto> budgetProgress = budgetService.getBudgetProgressList(
                userId, currentMonth, currentYear);

        return DashboardDto.builder()
                .totalBalance(totalBalance)
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .savingsRate(savingsRate)
                .recentTransactions(recentTransactions)
                .categoryBreakdown(categoryBreakdown)
                .monthlyTrend(monthlyTrend)
                .budgetProgress(budgetProgress)
                .build();
    }

    private List<MonthlySummaryDto> buildMonthlyTrend(Long userId, int month, int year, int months) {
        List<MonthlySummaryDto> trend = new ArrayList<>();
        LocalDate date = LocalDate.of(year, month, 1);

        for (int i = 0; i < months; i++) {
            LocalDate targetDate = date.minusMonths(i);
            MonthlySummaryDto summary = transactionService.getMonthlySummary(
                    userId, targetDate.getMonthValue(), targetDate.getYear());
            trend.add(summary);
        }

        return trend.reversed();
    }
}
