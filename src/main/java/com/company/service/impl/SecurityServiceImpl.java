package com.company.service.impl;

import com.company.dto.UserDto;
import com.company.entity.User;
import com.company.entity.common.UserPrincipal;
import com.company.repository.UserRepository;
import com.company.service.SecurityService;
import com.company.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;
    private final UserService userService;


    public SecurityServiceImpl(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userRepository.findByUsername(username).orElseThrow();

        if(user==null){
            throw new UsernameNotFoundException(username);
        }

        return new UserPrincipal(user);// get the user from db, and convert it to "user" that spring understands by using userPrincipal class
    }

    @Override
    public UserDto getLoggedInUser() {
        var currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(currentUsername);
    }

}

