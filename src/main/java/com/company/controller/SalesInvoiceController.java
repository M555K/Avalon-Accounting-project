package com.company.controller;

import com.company.enums.InvoiceType;
import com.company.service.CompanyService;
import com.company.service.InvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/salesInvoices")
public class SalesInvoiceController {

    private final InvoiceService invoiceService;
    private final CompanyService companyService;

    public SalesInvoiceController(InvoiceService invoiceService, CompanyService companyService) {
        this.invoiceService = invoiceService;
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String getAllSalesInvoices (Model model){

        model.addAttribute("invoices" , invoiceService.listAllByCompanyAndInvoiceType(InvoiceType.SALES));
        return "/invoice/sales-invoice-list";


    }

    // @GetMapping("/list")
    // public String getAllSalesInvoices (Model model){

    //     model.addAttribute("invoices",invoiceService.listAllByInvoiceType(InvoiceType.SALES));
    //     return "/invoice/sales-invoice-list";
    // }
}

