package com.company.service.impl;

import com.company.dto.UserDto;
import com.company.entity.User;
import com.company.exeptions.UserNotFountException;
import com.company.repository.UserRepository;
import com.company.service.UserService;
import com.company.util.MapperUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MapperUtil mapperUtil;

    public UserServiceImpl(UserRepository userRepository, MapperUtil mapperUtil) {
        this.userRepository = userRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UserNotFountException("User is not found"));

        return mapperUtil.convert(user, new UserDto());
    }
}
