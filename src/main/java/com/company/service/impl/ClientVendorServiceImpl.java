package com.company.service.impl;

import com.company.dto.ClientVendorDto;
import com.company.entity.ClientVendor;
import com.company.exeptions.ClientVendorNotFoundException;
import com.company.exeptions.UserNotFountException;
import com.company.repository.ClientVendorRepository;
import com.company.service.ClientVendorService;
import com.company.util.MapperUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientVendorServiceImpl implements ClientVendorService {

    private final MapperUtil mapperUtil;
   private final ClientVendorRepository clientVendorRepository;

    public ClientVendorServiceImpl(MapperUtil mapperUtil,@Lazy ClientVendorRepository clientVendorRepository) {
        this.mapperUtil = mapperUtil;
        this.clientVendorRepository = clientVendorRepository;
    }


    @Override
    public List<ClientVendorDto> listAllClientVendors() {
        List<ClientVendor> clientVendors = clientVendorRepository.findAll();
        return clientVendors.stream()
                .map(clientVendor -> mapperUtil.convert(clientVendor, new ClientVendorDto()))
                .collect(Collectors.toList());
    }

    @Override
    public ClientVendorDto findById(Long id) {
        ClientVendor clientVendor = clientVendorRepository.findById(id).orElseThrow(()-> new ClientVendorNotFoundException("Client/Vendor not found..."));

        return null;
    }
}
