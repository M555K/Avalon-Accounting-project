package com.company.service.impl;

import com.company.dto.RoleDto;
import com.company.entity.Role;
import com.company.repository.RoleRepository;
import com.company.service.RoleService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final MapperUtil mapperUtil;
    private final RoleRepository roleRepository;

    public RoleServiceImpl(MapperUtil mapperUtil, RoleRepository roleRepository) {
        this.mapperUtil = mapperUtil;
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDto findById(Long id) {
        Optional<Role> roleOptional = roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            return mapperUtil.convert(role, new RoleDto());
        } else {
            // Handle the case when the Role is not found
            // You can throw an exception or return a default value
            throw new RuntimeException("Role not found with id: " + id);
        }
    }
}
