package com.company.service;

import com.company.dto.CategoryDto;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {

    CategoryDto findCategoryById(Long id);
}
