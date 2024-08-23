package com.company.service;

import com.company.dto.AddressDto;
import com.company.entity.Address;

public interface AddressService {
    Long saveAndRetrieveId(AddressDto address);
    Address findById(Long id);
}
