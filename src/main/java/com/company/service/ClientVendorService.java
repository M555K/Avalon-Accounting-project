package com.company.service;


import com.company.dto.ClientVendorDto;

import java.util.List;

public interface ClientVendorService {

    List<ClientVendorDto> listAllClientVendors();

    ClientVendorDto findById(Long id);

}
