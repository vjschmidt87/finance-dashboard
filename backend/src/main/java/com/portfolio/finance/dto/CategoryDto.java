package com.portfolio.finance.dto;

import com.portfolio.finance.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String icon;

    @NotNull(message = "Category type is required")
    private Category.CategoryType type;
}
