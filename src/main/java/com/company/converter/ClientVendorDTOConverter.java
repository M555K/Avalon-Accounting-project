package com.company.converter;

import com.company.dto.ClientVendorDto;
import com.company.service.ClientVendorService;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding
public class ClientVendorDTOConverter implements Converter<String,ClientVendorDto> {

    private final ClientVendorService clientVendorService;

    public ClientVendorDTOConverter(@Lazy ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @Override
    public ClientVendorDto convert(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        return clientVendorService.findById(Long.parseLong(id));
    }


}
