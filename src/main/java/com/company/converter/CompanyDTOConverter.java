package com.company.converter;

import com.company.dto.CompanyDto;
import com.company.service.CompanyService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CompanyDTOConverter implements Converter <String, CompanyDto> {

    private final CompanyService companyService;

    public CompanyDTOConverter(CompanyService companyService) {
        this.companyService = companyService;
    }


    @Override
    public CompanyDto convert(String source) {
        return companyService.findById(Long.parseLong(source));
    }

}
