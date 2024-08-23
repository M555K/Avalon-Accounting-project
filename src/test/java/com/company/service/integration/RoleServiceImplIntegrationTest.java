package com.company.service.integration;

import com.company.dto.RoleDto;
import com.company.dto.UserDto;
import com.company.entity.Role;
import com.company.entity.User;
import com.company.exception.RoleNotFoundException;
import com.company.repository.RoleRepository;
import com.company.repository.UserRepository;
import com.company.service.RoleService;
import com.company.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class RoleServiceImplIntegrationTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save users
        User rootUser = new User();
        rootUser.setInsertUserId(1L);
        rootUser.setLastUpdateUserId(1L);
        userRepository.save(rootUser);

        // Create and save roles
        Role roleRoot = new Role();
        roleRoot.setId(1L);
        roleRoot.setDescription("root user");
        roleRoot.setInsertUserId(1L);
        roleRoot.setLastUpdateUserId(1L);

        Role roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setDescription("admin");
        roleAdmin.setInsertUserId(1L);
        roleAdmin.setLastUpdateUserId(1L);

        Role roleManager = new Role();
        roleManager.setId(3L);
        roleManager.setDescription("manager");
        roleManager.setInsertUserId(1L);
        roleManager.setLastUpdateUserId(1L);

        Role roleEmployee = new Role();
        roleEmployee.setId(4L);
        roleEmployee.setDescription("employee");
        roleEmployee.setInsertUserId(1L);
        roleEmployee.setLastUpdateUserId(1L);



        roleRepository.saveAll(List.of(roleRoot, roleAdmin, roleManager, roleEmployee));
    }

    @Test
    public void test_Find_By_Id_Role_Found() {
        // Act
        RoleDto result = roleService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("root user", result.getDescription());
    }

    @Test
    public void test_Find_By_Id_Role_Not_Found() {
        // Act & Assert
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> {
            roleService.findById(999L);
        });

        assertEquals("Role not found with id: 999", exception.getMessage());
    }

    @Test
    public void test_List_All_Roles() {
        // Act
        List<RoleDto> result = roleService.listAllRoles();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    public void test_List_Admin_Roles_As_Root_User() {
        // Arrange
        when(securityService.getLoggedInUser()).thenReturn(new UserDto() {{
            setRole(new RoleDto() {{
                setId(1L);
                setDescription("root user");
            }});
        }});

        // Act
        List<RoleDto> result = roleService.listAdminRoles();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getDescription());
    }

    @Test
    public void test_List_Admin_Roles_As_Regular_User() {
        // Arrange
        when(securityService.getLoggedInUser()).thenReturn(new UserDto() {{
            setRole(new RoleDto() {{
                setDescription("user");
            }});
        }});

        // Act
        List<RoleDto> result = roleService.listAdminRoles();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
//        assertEquals("admin", result.get(0).getDescription());
        assertTrue(result.stream().anyMatch(role -> role.getDescription().equalsIgnoreCase("admin")));
    }


    @Test
    public void test_List_Admin_Roles_when_No_Roles_Available() {
        // Arrange
        roleRepository.deleteAll(); // Clear all roles

        when(securityService.getLoggedInUser()).thenReturn(new UserDto() {{
            setRole(new RoleDto() {{
                setDescription("user");
            }});
        }});

        // Act
        List<RoleDto> result = roleService.listAdminRoles();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
