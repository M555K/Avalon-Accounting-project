package com.company.converter;

import com.company.dto.RoleDto;
import com.company.service.RoleService;
import org.springframework.core.convert.converter.Converter;

public class RoleDTOConverter implements Converter<String, RoleDto> {

    private final RoleService roleService;

    public RoleDTOConverter(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public RoleDto convert(String source) {
        return roleService.findById(Long.parseLong(source));
    }
}
