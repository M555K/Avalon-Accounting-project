package com.company.service;

import com.company.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto findByUsername(String username);
    List<UserDto> listAllUsers();
}
