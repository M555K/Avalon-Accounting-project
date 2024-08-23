package com.company.converter;

import com.company.dto.CompanyDto;
import com.company.service.CompanyService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CompanyDTOConverter implements Converter<String, CompanyDto> {

    private final CompanyService companyService;

    public CompanyDTOConverter(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public CompanyDto convert(String source) {
        if (source == null || source.isEmpty()){
            return null;
        }

        return companyService.findById(Long.parseLong(source));
    }
}