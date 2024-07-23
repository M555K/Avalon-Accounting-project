package com.company.service;

import com.company.dto.CompanyDto;
import org.springframework.stereotype.Service;


public interface CompanyService {

    CompanyDto findById (Long id);
}
