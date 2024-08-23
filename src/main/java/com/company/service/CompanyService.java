package com.company.service;

import com.company.dto.CompanyDto;
import com.company.enums.CompanyStatus;

import java.util.List;

public interface CompanyService {
   CompanyDto getCompanyByLoggedInUser();
   CompanyDto save(CompanyDto dto);
   CompanyDto update(CompanyDto dto);

   List<CompanyDto> getCompaniesByStatus (CompanyStatus status);
   List<CompanyDto> getCompaniesSortedByStatusAndTitle();
   List<CompanyDto> getCompaniesExcluding(Long id);
   List<CompanyDto> getAdminCompanies();

   CompanyDto findById(Long id);
  
   void activateCompany(Long companyId);
   void deactivateCompany(Long companyId);

}
