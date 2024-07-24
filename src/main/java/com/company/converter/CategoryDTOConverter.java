package com.company.converter;

import com.company.dto.CategoryDto;
import com.company.service.CategoryService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryDTOConverter implements Converter <String, CategoryDto> {

    private final CategoryService categoryService;

    public CategoryDTOConverter(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CategoryDto convert(String source) {
        return categoryService.findCategoryById(Long.parseLong(source));
    }
}
