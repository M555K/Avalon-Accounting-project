package com.company.service.integration;

import com.company.dto.UserDto;
import com.company.entity.User;
import com.company.exception.UserNotFoundException;
import com.company.repository.UserRepository;
import com.company.service.UserService;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MapperUtil mapperUtil;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        userDto = new UserDto();
        userDto.setId(1L);
    }

    @Test
    void testFindByUsername_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(mapperUtil.convert(any(User.class), any(UserDto.class))).thenReturn(userDto);

        UserDto foundUser = userService.findByUsername("username");

        assertNotNull(foundUser);
        assertEquals(userDto.getId(), foundUser.getId());
        verify(userRepository).findByUsername(anyString());
    }

    @Test
    void testFindByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.findByUsername("username"));
        verify(userRepository).findByUsername(anyString());
    }

    @Test
    void testSave_ShouldReturnSavedUserDto() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(mapperUtil.convert(any(UserDto.class), any(User.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapperUtil.convert(any(User.class), any(UserDto.class))).thenReturn(userDto);

        UserDto savedUserDto = userService.save(userDto);

        assertNotNull(savedUserDto);
        assertEquals(userDto.getId(), savedUserDto.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testFindById_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapperUtil.convert(any(User.class), any(UserDto.class))).thenReturn(userDto);

        UserDto foundUser = userService.findById(1L);

        assertNotNull(foundUser);
        assertEquals(userDto.getId(), foundUser.getId());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void testFindById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void testUpdateUser_ShouldSaveUpdatedUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapperUtil.convert(any(UserDto.class), any(User.class))).thenReturn(user);

        userService.updateUser(userDto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUser_ShouldMarkUserAsDeleted() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        assertTrue(user.getIsDeleted());
        verify(userRepository).save(any(User.class));
    }
}
