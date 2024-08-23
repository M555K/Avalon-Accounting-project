package com.company.service.unit;

import com.company.dto.RoleDto;
import com.company.dto.UserDto;
import com.company.entity.Role;
import com.company.exception.RoleNotFoundException;
import com.company.repository.RoleRepository;
import com.company.service.SecurityService;
import com.company.service.impl.RoleServiceImpl;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplUnitTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role roleAdmin;
    private Role roleManager;
    private Role roleEmployee;
    private Role roleRoot;

    private RoleDto roleDtoAdmin;
    private RoleDto roleDtoManager;
    private RoleDto roleDtoEmployee;
    private RoleDto roleDtoRoot;

    private UserDto rootUser;
    private UserDto regularUser;

    @BeforeEach
    public void setUp() {
        roleRoot = new Role();
        roleRoot.setId(1L);
        roleRoot.setDescription("root user");

        roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setDescription("admin");

        roleManager = new Role();
        roleManager.setId(3L);
        roleManager.setDescription("manager");

        roleEmployee = new Role();
        roleEmployee.setId(4L);
        roleEmployee.setDescription("employee");

        roleDtoRoot = new RoleDto();
        roleDtoRoot.setId(1L);
        roleDtoRoot.setDescription("root user");

        roleDtoAdmin = new RoleDto();
        roleDtoAdmin.setId(2L);
        roleDtoAdmin.setDescription("admin");

        roleDtoManager = new RoleDto();
        roleDtoManager.setId(3L);
        roleDtoManager.setDescription("manager");

        roleDtoEmployee = new RoleDto();
        roleDtoEmployee.setId(4L);
        roleDtoEmployee.setDescription("employee");

        rootUser = new UserDto();
        rootUser.setRole(roleDtoRoot);

        regularUser = new UserDto();
        regularUser.setRole(roleDtoManager); // Assign regular user to a non-root role
    }

    @Test
    public void test_Find_By_Id_Role_Found() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.of(roleRoot));
        when(mapperUtil.convert(eq(roleRoot), any(RoleDto.class))).thenReturn(roleDtoRoot);

        // Act
        RoleDto result = roleService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(roleDtoRoot.getId(), result.getId());
        assertEquals(roleDtoRoot.getDescription(), result.getDescription());

        // Verify that the mocked methods were called
        verify(roleRepository).findById(1L);
        verify(mapperUtil).convert(eq(roleRoot), any(RoleDto.class));
    }

    @Test
    public void test_Find_By_Id_Role_Not_Found() {
        // Arrange
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> {
            roleService.findById(1L);
        });

        assertEquals("Role not found with id: 1", exception.getMessage());

        // Verify that the mocked method was called
        verify(roleRepository).findById(1L);
    }

    @Test
    public void test_List_All_Roles() {
        // Arrange
        List<Role> roleList = Arrays.asList(roleRoot, roleAdmin, roleManager, roleEmployee);
        when(roleRepository.findAll()).thenReturn(roleList);
        when(mapperUtil.convert(eq(roleRoot), any(RoleDto.class))).thenReturn(roleDtoRoot);
        when(mapperUtil.convert(eq(roleAdmin), any(RoleDto.class))).thenReturn(roleDtoAdmin);
        when(mapperUtil.convert(eq(roleManager), any(RoleDto.class))).thenReturn(roleDtoManager);
        when(mapperUtil.convert(eq(roleEmployee), any(RoleDto.class))).thenReturn(roleDtoEmployee);

        // Act
        List<RoleDto> result = roleService.listAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(roleDtoRoot.getId(), result.get(0).getId());
        assertEquals(roleDtoRoot.getDescription(), result.get(0).getDescription());
        assertEquals(roleDtoAdmin.getId(), result.get(1).getId());
        assertEquals(roleDtoAdmin.getDescription(), result.get(1).getDescription());
        assertEquals(roleDtoManager.getId(), result.get(2).getId());
        assertEquals(roleDtoManager.getDescription(), result.get(2).getDescription());
        assertEquals(roleDtoEmployee.getId(), result.get(3).getId());
        assertEquals(roleDtoEmployee.getDescription(), result.get(3).getDescription());

        // Verify that the mocked methods were called
        verify(roleRepository).findAll();
        verify(mapperUtil).convert(eq(roleRoot), any(RoleDto.class));
        verify(mapperUtil).convert(eq(roleAdmin), any(RoleDto.class));
        verify(mapperUtil).convert(eq(roleManager), any(RoleDto.class));
        verify(mapperUtil).convert(eq(roleEmployee), any(RoleDto.class));
    }

    @Test
    public void test_List_All_Roles_Empty() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<RoleDto> result = roleService.listAllRoles();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify that the mocked methods were called
        verify(roleRepository).findAll();
        verify(mapperUtil, never()).convert(any(Role.class), any(RoleDto.class));
    }

    @Test
    public void test_List_Admin_Roles_if_login_As_Root_User() {
        // Arrange
        when(securityService.getLoggedInUser()).thenReturn(rootUser);
        List<Role> roleList = Arrays.asList(roleAdmin, roleManager, roleEmployee, roleRoot);
        when(roleRepository.findAll()).thenReturn(roleList);
        when(mapperUtil.convert(eq(roleAdmin), any(RoleDto.class))).thenReturn(roleDtoAdmin);

        // Act
        List<RoleDto> result = roleService.listAdminRoles();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(roleDtoAdmin.getId(), result.get(0).getId());
        assertEquals(roleDtoAdmin.getDescription(), result.get(0).getDescription());

        // Verify that the mocked methods were called
        verify(securityService).getLoggedInUser();
        verify(roleRepository).findAll();
        verify(mapperUtil).convert(eq(roleAdmin), any(RoleDto.class));
    }

    @Test
    public void test_List_Admin_Roles_As_Non_Root_User() {
        // Arrange
        when(securityService.getLoggedInUser()).thenReturn(regularUser);
        List<Role> roleList = Arrays.asList(roleAdmin, roleManager, roleEmployee, roleRoot);
        when(roleRepository.findAll()).thenReturn(roleList);
        when(mapperUtil.convert(eq(roleAdmin), any(RoleDto.class))).thenReturn(roleDtoAdmin);
        when(mapperUtil.convert(eq(roleManager), any(RoleDto.class))).thenReturn(roleDtoManager);
        when(mapperUtil.convert(eq(roleEmployee), any(RoleDto.class))).thenReturn(roleDtoEmployee);

        // Act
        List<RoleDto> result = roleService.listAdminRoles();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(roleDtoAdmin.getId(), result.get(0).getId());
        assertEquals(roleDtoAdmin.getDescription(), result.get(0).getDescription());
        assertEquals(roleDtoManager.getId(), result.get(1).getId());
        assertEquals(roleDtoManager.getDescription(), result.get(1).getDescription());
        assertEquals(roleDtoEmployee.getId(), result.get(2).getId());
        assertEquals(roleDtoEmployee.getDescription(), result.get(2).getDescription());

        // Verify that the mocked methods were called
        verify(securityService).getLoggedInUser();
        verify(roleRepository).findAll();
        verify(mapperUtil).convert(eq(roleAdmin), any(RoleDto.class));
        verify(mapperUtil).convert(eq(roleManager), any(RoleDto.class));
        verify(mapperUtil).convert(eq(roleEmployee), any(RoleDto.class));
    }

    @Test
    public void test_List_Admin_Roles_when_No_Roles_is_available() {
        // Arrange
        when(securityService.getLoggedInUser()).thenReturn(regularUser);
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<RoleDto> result = roleService.listAdminRoles();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify that the mocked methods were called
        verify(securityService).getLoggedInUser();
        verify(roleRepository).findAll();
        verify(mapperUtil, never()).convert(any(Role.class), any(RoleDto.class));
    }
}
