package com.portfolio.finance.controller;

import com.portfolio.finance.dto.BudgetDto;
import com.portfolio.finance.dto.BudgetProgressDto;
import com.portfolio.finance.security.SecurityUtils;
import com.portfolio.finance.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<BudgetDto>> getBudgets(@RequestParam int month, @RequestParam int year) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getBudgets(userId, month, year));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<BudgetProgressDto> getBudgetProgress(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getBudgetProgress(id, userId));
    }

    @PostMapping
    public ResponseEntity<BudgetDto> createBudget(@Valid @RequestBody BudgetDto budgetDto) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(budgetDto, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDto> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetDto budgetDto) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.updateBudget(id, budgetDto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        budgetService.deleteBudget(id, userId);
        return ResponseEntity.noContent().build();
    }
}
