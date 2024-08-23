package com.company.service.unit;

import com.company.dto.CompanyDto;
import com.company.dto.RoleDto;
import com.company.dto.UserDto;
import com.company.entity.Company;
import com.company.entity.Role;
import com.company.entity.User;
import com.company.exception.UserNotFoundException;
import com.company.repository.UserRepository;
import com.company.service.SecurityService;
import com.company.service.impl.UserServiceImpl;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MapperUtil mapperUtil;
    @Mock
    private SecurityService securityService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    User employeeUser;
    User adminUser;
    User rootAdminUser;

    UserDto employeeUserDto;
    UserDto adminUserDto;
    UserDto rootAdminUserDto;

    Role employeeRole;
    Role rootUserRole;
    Role adminRole;

    RoleDto employeeRoleDto;
    RoleDto rootUserRoleDto;
    RoleDto adminRoleDto;


    Company company;

    CompanyDto companyDto;


    @BeforeEach
    void setUp() {

        employeeRole = new Role();
        employeeRole.setDescription("Employee");

        adminRole = new Role();
        adminRole.setDescription("Admin");

        rootUserRole = new Role();
        rootUserRole.setDescription("Root User");


        employeeUser = new User();
        employeeUser.setId(1L);
        employeeUser.setUsername("employee@gmail.com");
        employeeUser.setPassword("password");
        employeeUser.setRole(employeeRole);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin@gmail.com");
        adminUser.setPassword("password");
        adminUser.setRole(adminRole);

        rootAdminUser = new User();
        rootAdminUser.setId(3L);
        rootAdminUser.setUsername("admin@gmail.com");
        rootAdminUser.setPassword("password");
        rootAdminUser.setRole(rootUserRole);


        employeeRoleDto = new RoleDto();
        employeeRoleDto.setDescription("Employee");

        adminRoleDto = new RoleDto();
        adminRoleDto.setDescription("Admin");

        rootUserRoleDto = new RoleDto();
        rootUserRoleDto.setDescription("Root User");

        employeeUserDto = new UserDto();
        employeeUserDto.setId(1L);
        employeeUserDto.setUsername("employee@gmail.com");
        employeeUserDto.setPassword("password");
        employeeUserDto.setRole(employeeRoleDto);
        employeeUserDto.setOnlyAdmin(true);

        adminUserDto = new UserDto();
        adminUserDto.setId(2L);
        adminUserDto.setUsername("admin@gmail.com");
        adminUserDto.setPassword("password");
        adminUserDto.setRole(adminRoleDto);
        adminUserDto.setOnlyAdmin(true);

        rootAdminUserDto = new UserDto();
        rootAdminUserDto.setId(3L);
        rootAdminUserDto.setUsername("admin@gmail.com");
        rootAdminUserDto.setPassword("password");
        rootAdminUserDto.setRole(rootUserRoleDto);
        rootAdminUserDto.setOnlyAdmin(true);


        company = new Company();
        company.setTitle("company");

        companyDto = new CompanyDto();
        companyDto.setTitle("company");


        employeeUser.setCompany(company);
        adminUser.setCompany(company);
        rootAdminUser.setCompany(company);

        employeeUserDto.setCompany(companyDto);
        adminUserDto.setCompany(companyDto);
        rootAdminUserDto.setCompany(companyDto);
    }


    @Test
    void findByUsernameUnSuccess() {

        when(userRepository.findByUsername(employeeUser.getUsername())).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.findByUsername(employeeUser.getUsername()));
    }

    @Test
    void findByUsernameSuccess() {
        when(userRepository.findByUsername(employeeUser.getUsername())).thenReturn(employeeUser);
        when(mapperUtil.convert(eq(employeeUser), any(UserDto.class))).thenReturn(employeeUserDto);

        UserDto result = userServiceImpl.findByUsername(employeeUser.getUsername());

        assertEquals(employeeUserDto, result);

        verify(userRepository, times(1)).findByUsername(employeeUser.getUsername());
        verify(mapperUtil, times(1)).convert(eq(employeeUser), any(UserDto.class));
    }


    @Test
    void listAllUsersIfRootAdmin() {

        when(securityService.getLoggedInUser()).thenReturn(rootAdminUserDto);
        when(userRepository.findAll()).thenReturn(List.of(adminUser));

        when(mapperUtil.convert(any(User.class), any(UserDto.class))).thenReturn(adminUserDto);


        List<UserDto> result = userServiceImpl.listAllUsers();

        assertEquals(adminUserDto, result.get(0));
        assertEquals(1, result.size());

        verify(userRepository, times(1)).findAll();
        verify(mapperUtil, times(1)).convert(eq(adminUser), any(UserDto.class));
        verify(securityService, times(1)).getLoggedInUser();
    }

    @Test
    void listAllUsersIfAdmin() {

        when(securityService.getLoggedInUser()).thenReturn(adminUserDto);
        when(userRepository.findAll()).thenReturn(List.of(employeeUser));

        when(mapperUtil.convert(any(User.class), any(UserDto.class))).thenReturn(employeeUserDto);


        List<UserDto> result = userServiceImpl.listAllUsers();

        assertEquals(employeeUserDto, result.get(0));
        assertEquals(1, result.size());

        verify(userRepository, times(1)).findAll();
        verify(mapperUtil, times(1)).convert(eq(employeeUser), any(UserDto.class));
        verify(securityService, times(1)).getLoggedInUser();
    }

    @Test
    void saveIfEmailExists() {

        when(userRepository.findByUsername(employeeUserDto.getUsername())).thenReturn(employeeUser);

        assertThrows(IllegalArgumentException.class, () -> userServiceImpl.save(employeeUserDto));

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    void saveTest() {

        when(userRepository.findByUsername(employeeUserDto.getUsername())).thenReturn(null);
        when(mapperUtil.convert(any(UserDto.class), any(User.class))).thenReturn(employeeUser);
        when(passwordEncoder.encode(employeeUserDto.getPassword())).thenReturn("encodedPassword");

        userServiceImpl.save(employeeUserDto);

        verify(userRepository, times(1)).findByUsername(employeeUserDto.getUsername());
        verify(mapperUtil, times(1)).convert(eq(employeeUserDto), any(User.class));
        verify(passwordEncoder, times(1)).encode(employeeUserDto.getPassword());
        verify(userRepository, times(1)).save(employeeUser);


        assertTrue(employeeUser.isEnabled());
        assertEquals("encodedPassword", employeeUser.getPassword());

    }

    @Test
    void findByIdIfNull() {
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.findById(employeeUser.getId()));
    }

    @Test
    void findByIdTest() {

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(mapperUtil.convert(any(User.class), any(UserDto.class))).thenReturn(adminUserDto);

        UserDto result = userServiceImpl.findById(adminUser.getId());

        assertEquals(adminUserDto.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findById(adminUser.getId());
        verify(mapperUtil, times(1)).convert(eq(adminUser), any(UserDto.class));


    }

    @Test
    void updateUser() {

        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));
        when(mapperUtil.convert(any(UserDto.class), any(User.class))).thenReturn(employeeUser);
        userServiceImpl.updateUser(employeeUserDto);

        verify(userRepository, times(1)).findById(employeeUser.getId());
        verify(mapperUtil, times(1)).convert(eq(employeeUserDto), any(User.class));
        verify(userRepository, times(1)).save(employeeUser);

    }

    @Test
    void deleteIfUserNotFound() {

        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userServiceImpl.findById(employeeUser.getId()));

    }

    @Test
    void deleteIfUserFound() {

        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        userServiceImpl.deleteUser(employeeUser.getId());

        verify(userRepository, times(1)).findById(employeeUser.getId());
        verify(userRepository, times(1)).save(employeeUser);


        assertEquals("temp_" + employeeUser.getId() + "@example.com", employeeUser.getUsername());
        assertTrue(employeeUser.getIsDeleted());

    }


}