package com.company.service;

import com.company.dto.CompanyDto;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CompanyService {

    CompanyDto findById (Long id);

    List<CompanyDto> findAllCompanies();
}
