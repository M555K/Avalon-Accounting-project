package com.company.service;

import com.company.dto.RoleDto;
import com.company.dto.UserDto;
import com.company.entity.User;

import java.util.List;

public interface RoleService {


    RoleDto findById(Long id);
    List<RoleDto> listAllRoles();
    List<RoleDto> listAdminRoles();

}
