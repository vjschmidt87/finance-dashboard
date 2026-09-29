package com.portfolio.finance.service;

import com.portfolio.finance.dto.BudgetDto;
import com.portfolio.finance.dto.BudgetProgressDto;
import com.portfolio.finance.entity.Budget;
import com.portfolio.finance.entity.Category;
import com.portfolio.finance.entity.User;
import com.portfolio.finance.repository.BudgetRepository;
import com.portfolio.finance.repository.CategoryRepository;
import com.portfolio.finance.repository.TransactionRepository;
import com.portfolio.finance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<BudgetDto> getBudgets(Long userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public BudgetDto createBudget(BudgetDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Budget budget = Budget.builder()
                .name(dto.getName())
                .limitAmount(dto.getLimitAmount())
                .category(category)
                .month(dto.getMonth())
                .year(dto.getYear())
                .user(user)
                .build();

        Budget saved = budgetRepository.save(budget);
        log.info("Created budget '{}' (id={}) for user {}", saved.getName(), saved.getId(), userId);
        return toDto(saved);
    }

    @Transactional
    public BudgetDto updateBudget(Long id, BudgetDto dto, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        budget.setName(dto.getName());
        budget.setLimitAmount(dto.getLimitAmount());
        budget.setCategory(category);
        budget.setMonth(dto.getMonth());
        budget.setYear(dto.getYear());

        Budget saved = budgetRepository.save(budget);
        log.info("Updated budget id={} for user {}", id, userId);
        return toDto(saved);
    }

    @Transactional
    public void deleteBudget(Long id, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));
        budgetRepository.delete(budget);
        log.info("Deleted budget id={} for user {}", id, userId);
    }

    @Transactional(readOnly = true)
    public BudgetProgressDto getBudgetProgress(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));

        return calculateProgress(budget);
    }

    @Transactional(readOnly = true)
    public List<BudgetProgressDto> getBudgetProgressList(Long userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(this::calculateProgress)
                .toList();
    }

    private BudgetProgressDto calculateProgress(Budget budget) {
        LocalDate start = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal spentAmount = transactionRepository.sumByCategoryAndDateRange(
                budget.getUser().getId(), budget.getCategory().getId(), start, end);

        if (spentAmount == null) {
            spentAmount = BigDecimal.ZERO;
        }

        BigDecimal remainingAmount = budget.getLimitAmount().subtract(spentAmount);
        double percentUsed = budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0
                ? spentAmount.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        return BudgetProgressDto.builder()
                .id(budget.getId())
                .name(budget.getName())
                .limitAmount(budget.getLimitAmount())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .month(budget.getMonth())
                .year(budget.getYear())
                .spentAmount(spentAmount)
                .remainingAmount(remainingAmount)
                .percentUsed(percentUsed)
                .build();
    }

    private BudgetDto toDto(Budget budget) {
        return BudgetDto.builder()
                .id(budget.getId())
                .name(budget.getName())
                .limitAmount(budget.getLimitAmount())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .month(budget.getMonth())
                .year(budget.getYear())
                .build();
    }
}
