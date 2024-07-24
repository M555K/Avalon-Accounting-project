package com.company.service.impl;

import com.company.dto.CategoryDto;
import com.company.entity.Category;
import com.company.repository.CategoryRepository;
import com.company.service.CategoryService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final MapperUtil mapperUtil;
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(MapperUtil mapperUtil, CategoryRepository categoryRepository) {
        this.mapperUtil = mapperUtil;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public CategoryDto findCategoryById(Long id) {

        Category foundCategory = categoryRepository.findById(id)
                .orElseThrow(()->new RuntimeException("category not found."));
        return mapperUtil.convert(foundCategory,new CategoryDto());
    }
}
