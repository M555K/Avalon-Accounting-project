package com.company.converter;

import com.company.dto.ClientVendorDto;
import com.company.service.ClientVendorService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientVendorDTOConverter implements Converter<String, ClientVendorDto> {

    private final ClientVendorService clientVendorService;

    public ClientVendorDTOConverter(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @Override
    public ClientVendorDto convert(String source) {
        if (source==null || source.isEmpty()) {
            return null;
        }
        return clientVendorService.findById(Long.valueOf(source));
    }
}
