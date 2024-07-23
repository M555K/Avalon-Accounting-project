package com.company.service.impl;

import com.company.dto.CompanyDto;
import com.company.entity.Company;
import com.company.exeptions.CompanyNotFoundException;
import com.company.repository.CompanyRepository;
import com.company.service.CompanyService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final MapperUtil mapperUtil;

    public CompanyServiceImpl(CompanyRepository companyRepository, MapperUtil mapperUtil) {
        this.companyRepository = companyRepository;
        this.mapperUtil = mapperUtil;
    }


    @Override
    public CompanyDto findById(Long id) {

        Company foundCompany = companyRepository.findCompanyById(id).orElseThrow(() -> new CompanyNotFoundException("No Address Found!"));

        return mapperUtil.convert(foundCompany, new CompanyDto());


    }
}
