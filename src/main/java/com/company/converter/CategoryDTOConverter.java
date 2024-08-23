package com.company.converter;

import com.company.dto.CategoryDto;
import com.company.service.CategoryService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryDTOConverter implements Converter<String, CategoryDto> {


    CategoryService categoryService;

    public CategoryDTOConverter(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public CategoryDto convert(String source) {

        if (source == null || source.equals("")) {
            return null;
        }

        return categoryService.findById(Long.valueOf(source));
    }




}
