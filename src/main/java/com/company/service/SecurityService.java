package com.company.service;

import com.company.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SecurityService extends UserDetailsService {
    UserDto getLoggedInUser();
}
