package com.company.service.impl;

import com.company.dto.ClientVendorDto;
import com.company.entity.ClientVendor;
import com.company.entity.Company;
import com.company.enums.ClientVendorType;
import com.company.exception.ClientVendorNotFoundException;
import com.company.repository.ClientVendorRepository;
import com.company.service.*;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final ClientVendorRepository clientVendorRepository;
    private final CompanyService companyService;
    private final MapperUtil mapperUtil;
    private final AddressService addressService;
    private final InvoiceService invoiceService;

    public ClientVendorServiceImpl(ClientVendorRepository clientVendorRepository, CompanyService companyService,
                                   MapperUtil mapperUtil, AddressService addressService, InvoiceService invoiceService) {
        this.clientVendorRepository = clientVendorRepository;
        this.companyService = companyService;
        this.mapperUtil = mapperUtil;
        this.addressService = addressService;
        this.invoiceService = invoiceService;
    }


    @Override
    public ClientVendorDto findById(Long id) {
        ClientVendor findClientVendor = clientVendorRepository.findById(id)
                .orElseThrow(() -> new ClientVendorNotFoundException("No Client/Vendor found with Id - " + id));
        return mapperUtil.convert(findClientVendor, new ClientVendorDto());
    }

    @Override
    public List<ClientVendorDto> findClientVendorsByCompanyId(ClientVendorType clientVendorType) {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();
        List<ClientVendor> vendors = clientVendorRepository.findClientVendorsByCompanyId(companyId, clientVendorType);
        return vendors.stream()
                .map(vendor -> mapperUtil.convert(vendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientVendorDto> findClientVendorsByCompanyIdAndClientVendorType() {
        Long companyId = companyService.getCompanyByLoggedInUser().getId();
        List<ClientVendor> vendors = clientVendorRepository.findClientVendorsByCompanyIdAndClientVendorType(companyId);
        return vendors.stream()
                .map(vendor -> {
                    ClientVendorDto clientVendorDto = mapperUtil.convert(vendor, new ClientVendorDto());
                    if (invoiceService.hasInvoicesByClientVendorId(clientVendorDto.getId())) {
                        clientVendorDto.setHasInvoice(true);
                        return clientVendorDto;
                    }
                    return clientVendorDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ClientVendorDto saveClientVendor(ClientVendorDto clientVendor) {
        Long savedAddressId = addressService.saveAndRetrieveId(clientVendor.getAddress());
        ClientVendor newClientVendor = mapperUtil.convert(clientVendor, new ClientVendor());
        newClientVendor.setCompany(mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company()));
        newClientVendor.setAddress(addressService.findById(savedAddressId));
        clientVendorRepository.save(newClientVendor);
        return mapperUtil.convert(newClientVendor, new ClientVendorDto());
    }

    @Override
    public ClientVendorDto updateClientVendor(ClientVendorDto clientVendor) {

        ClientVendor clientVendorInDb = clientVendorRepository.findById(clientVendor.getId())
                .orElseThrow(() -> new ClientVendorNotFoundException("No Client/Vendor found with Id - " + clientVendor.getId()));
        Long addressId = clientVendorInDb.getAddress().getId();
        clientVendor.getAddress().setId(addressId);

        Long savedAddressId = addressService.saveAndRetrieveId(clientVendor.getAddress());

        ClientVendor updatedClientVendor = mapperUtil.convert(clientVendor, new ClientVendor());
        updatedClientVendor.setCompany(mapperUtil.convert(companyService.getCompanyByLoggedInUser(), new Company()));
        updatedClientVendor.setAddress(addressService.findById(savedAddressId));


        clientVendorRepository.save(updatedClientVendor);
        return mapperUtil.convert(updatedClientVendor, new ClientVendorDto());

    }

    @Override
    public void deleteClientVendor(Long id) {

        ClientVendor clientVendor = clientVendorRepository.findById(id)
                .orElseThrow(() -> new ClientVendorNotFoundException("No Client/Vendor found with Id - " + id));
        clientVendor.setIsDeleted(true);
        clientVendor.getAddress().setIsDeleted(true);
        clientVendorRepository.save(clientVendor);

    }

}
