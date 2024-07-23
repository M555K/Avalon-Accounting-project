package com.company.converter;

import com.company.dto.CompanyDto;
import com.company.service.CompanyService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CompanyDTOConverter implements Converter <Long, CompanyDto> {

    private final CompanyService companyService;

    public CompanyDTOConverter(CompanyService companyService) {
        this.companyService = companyService;
    }


    @Override
    public CompanyDto convert(Long source) {
        return companyService.findById(source);
    }
}
