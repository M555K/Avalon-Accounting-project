package com.company.controller;

import com.company.dto.CompanyDto;
import com.company.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;


    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String listAllCompanies(Model model){
        List<CompanyDto> allCompanies = companyService.findAllCompanies();
        model.addAttribute("companies",allCompanies);
        return "/company/company-list";
    }
}
