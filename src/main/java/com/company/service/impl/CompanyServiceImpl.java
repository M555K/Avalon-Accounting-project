package com.company.service.impl;

import com.company.dto.CompanyDto;
import com.company.entity.Company;
import com.company.exeptions.CompanyNotFoundException;
import com.company.repository.CompanyRepository;
import com.company.service.CompanyService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<CompanyDto> findAllCompanies() {

        List<Company> allCompanies = companyRepository.findAll();
        return mapperUtil.convert(allCompanies, new ArrayList<CompanyDto>());
    }
}
