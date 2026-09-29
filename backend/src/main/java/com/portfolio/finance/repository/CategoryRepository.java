package com.portfolio.finance.repository;

import com.portfolio.finance.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserIdOrUserIsNull(Long userId);

    List<Category> findByType(Category.CategoryType type);

    List<Category> findByTypeAndUserIdOrTypeAndUserIsNull(
            Category.CategoryType type1, Long userId,
            Category.CategoryType type2);
}
