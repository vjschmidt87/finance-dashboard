package com.portfolio.finance.controller;

import com.portfolio.finance.dto.CategoryDto;
import com.portfolio.finance.entity.Category;
import com.portfolio.finance.security.SecurityUtils;
import com.portfolio.finance.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(categoryService.getAllCategories(userId));
    }

    @GetMapping("/income")
    public ResponseEntity<List<CategoryDto>> getIncomeCategories() {
        return ResponseEntity.ok(categoryService.getCategoriesByType(Category.CategoryType.INCOME));
    }

    @GetMapping("/expense")
    public ResponseEntity<List<CategoryDto>> getExpenseCategories() {
        return ResponseEntity.ok(categoryService.getCategoriesByType(Category.CategoryType.EXPENSE));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryDto, userId));
    }
}
