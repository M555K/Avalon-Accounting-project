package com.company.converter;

import com.company.dto.UserDto;
import com.company.service.UserService;
import org.springframework.core.convert.converter.Converter;

public class UserDTOConverter implements Converter<String, UserDto> {

    private final UserService userService;

    public UserDTOConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDto convert(String source) {
        return userService.findByUsername(source);
    }
}
