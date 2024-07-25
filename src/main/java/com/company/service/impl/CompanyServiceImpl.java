package com.company.service.impl;

import com.company.dto.CompanyDto;
import com.company.entity.Company;
import com.company.exeptions.CompanyNotFoundException;
import com.company.repository.CompanyRepository;
import com.company.service.CompanyService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        // Fetch all companies from the repository
        List<Company> allCompanies = companyRepository.findAll();

        // Filter out the "CYDEO" company (ID=1)
        List<CompanyDto> companyDtos = allCompanies.stream()
                .filter(company -> company.getId() != 1) // Exclude company with ID=1
                .map(company -> mapperUtil.convert(company, new CompanyDto()))
                .collect(Collectors.toList());

        // Sorting by status (Active first) and then by title
        companyDtos.sort(Comparator.comparing((CompanyDto c) -> {
                    if (c.getCompanyStatus().getValue().equals("Active")) {
                        return 0;  // Active companies first
                    } else {
                        return 1;  // Passive companies after
                    }
                })
                .thenComparing(CompanyDto::getTitle));

        return companyDtos;
    }
    }

