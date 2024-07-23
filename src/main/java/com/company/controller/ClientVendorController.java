package com.company.controller;

import com.company.dto.ClientVendorDto;
import com.company.dto.CompanyDto;
import com.company.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {

    private final ClientVendorService clientVendorService;

    public ClientVendorController(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listAllCompanies(Model model){
        List<ClientVendorDto> allClientVendors = clientVendorService.listAllClientVendors();
        model.addAttribute("clientVendors",allClientVendors);
        return "/clientVendor/clientVendor-list";
    }

}
