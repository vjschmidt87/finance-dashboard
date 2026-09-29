package com.portfolio.finance.service;

import com.portfolio.finance.dto.CategoryDto;
import com.portfolio.finance.entity.Category;
import com.portfolio.finance.entity.User;
import com.portfolio.finance.repository.CategoryRepository;
import com.portfolio.finance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Long userId) {
        return categoryRepository.findByUserIdOrUserIsNull(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategoriesByType(Category.CategoryType type) {
        return categoryRepository.findByType(type).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Category category = Category.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .type(dto.getType())
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Created category '{}' (id={}) for user {}", saved.getName(), saved.getId(), userId);
        return toDto(saved);
    }

    private CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .type(category.getType())
                .build();
    }
}
