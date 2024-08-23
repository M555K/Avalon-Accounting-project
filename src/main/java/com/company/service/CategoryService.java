package com.company.service;

import com.company.dto.CategoryDto;
import com.company.dto.CompanyDto;
import com.company.dto.UserDto;
import com.company.entity.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto findById(Long id);

    List<CategoryDto> listAllCategories();

    void save(CategoryDto categoryDto);

    void update(CategoryDto categoryDto);

    void delete(Long id);
}
