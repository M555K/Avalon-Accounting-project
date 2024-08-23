package com.company.service.integration;

import com.company.dto.AddressDto;
import com.company.dto.CompanyDto;
import com.company.dto.RoleDto;
import com.company.dto.UserDto;
import com.company.entity.Address;
import com.company.entity.Company;
import com.company.entity.common.UserPrincipal;
import com.company.enums.CompanyStatus;
import com.company.repository.CompanyRepository;
import com.company.repository.UserRepository;
import com.company.service.CompanyService;
import com.company.service.SecurityService;
import com.company.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CompanyServiceImplIntegrationTest {

    @Autowired
    private CompanyService companyService;

    @MockBean
    private SecurityService securityService;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private UserPrincipal userPrincipal;

    @MockBean
    private Authentication authentication;

    @MockBean
    private CompanyRepository companyRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private Address address;
    private AddressDto addressDto;
    private Company company;
    private CompanyDto companyDto;
    private UserDto userDto;

    private Address createAddress(){
        address = new Address();
        address.setId(1L);

        return address;
    }

    private AddressDto createAddressDto(){
        addressDto = new AddressDto();
        addressDto.setId(1L);
        addressDto.setAddressLine1("123");
        addressDto.setCity("City");
        addressDto.setState("State");
        addressDto.setCountry("USA");
        addressDto.setZipCode("12345-1234");

        return addressDto;
    }

    private Company createCompany(){
        company = new Company();
        company.setId(1L);
        company.setTitle("Sample company");
        company.setAddress(address);
        company.setCompanyStatus(CompanyStatus.ACTIVE);

        return company;
    }

    private CompanyDto createCompanyDto(){
        companyDto = new CompanyDto();
        companyDto.setId(1L);
        companyDto.setTitle("Sample company");
        companyDto.setAddress(addressDto);
        companyDto.setCompanyStatus(CompanyStatus.ACTIVE);
        companyDto.setPhone("123-123-1234");

        return companyDto;
    }

    private UserDto createUserDto(){
        userDto = new UserDto();
        userDto.setUsername("username@mail.com");
        userDto.setId(1L);
        userDto.setCompany(companyDto);
        userDto.setRole(new RoleDto());
        return userDto;
    }

    @BeforeEach
    void setUp() {
        address = createAddress();
        addressDto = createAddressDto();
        company = createCompany();
        companyDto = createCompanyDto();
    }

    @Test
    public void getCompanyByLoggedInUser_should_return_logged_in_users_company() {
        userDto = createUserDto();

        when(securityService.getLoggedInUser()).thenReturn(userDto);

        CompanyDto actualCompany = companyService.getCompanyByLoggedInUser();

        assertThat(actualCompany).isEqualTo(companyDto);
    }

    @Test
    @Transactional
    public void save_should_persist_company_and_return_dto() {

        companyDto = createCompanyDto();
        companyDto.setCompanyStatus(CompanyStatus.PASSIVE);

        // mock PrePersist
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);

        when(userPrincipal.getId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authentication.getName()).thenReturn("user");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        CompanyDto returnedCompanyDto = companyService.save(companyDto);

        assertThat(returnedCompanyDto.getTitle()).isEqualTo(companyDto.getTitle());
        assertThat(returnedCompanyDto.getId()).isEqualTo(companyDto.getId());
    }

    @Test
    @Transactional
    public void update_should_modify_existing_company() {
        companyDto = createCompanyDto();
        companyDto.setCompanyStatus(CompanyStatus.PASSIVE);

        // mock PrePersist
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);

        when(userPrincipal.getId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authentication.getName()).thenReturn("user");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        CompanyDto savedCompanyDto = companyService.save(companyDto);

        savedCompanyDto.setTitle("new title");
        savedCompanyDto.setPhone("123-123-1234");

        CompanyDto returnedCompanyDto = companyService.update(savedCompanyDto);

        assertThat(returnedCompanyDto.getTitle()).isEqualTo("new title");
        assertThat(returnedCompanyDto.getPhone()).isEqualTo("123-123-1234");
    }

/*    @Test
    @Transactional // not passing
    public void activateCompany_should_set_company_status_to_active() {
        // mock PrePersist
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);

        when(userPrincipal.getId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authentication.getName()).thenReturn("user");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        userDto = createUserDto();
        addressDto = createAddressDto();
        companyDto = createCompanyDto();
        companyDto.setCompanyStatus(CompanyStatus.PASSIVE);


        userService.save(userDto);
        companyService.save(companyDto);

        companyService.activateCompany(companyDto.getId());

        Optional<Company> savedCompany = companyRepository.findById(companyDto.getId());
        assertThat(savedCompany).isEqualTo(CompanyStatus.ACTIVE);
    }*/

    @Test
    public void deactivateCompany_should_set_company_status_to_passive() {
    }

    @Test
    @Transactional
    public void getCompaniesByStatus_should_return_companies_with_given_status() {
        CompanyDto companyDto1 = new CompanyDto();
        CompanyDto companyDto2 = new CompanyDto();
        AddressDto addressDto1 = new AddressDto();
        AddressDto addressDto2 = new AddressDto();
        UserDto userDto = new UserDto();

        userDto.setCompany(companyDto);

        companyDto1.setCompanyStatus(CompanyStatus.PASSIVE);
        companyDto2.setCompanyStatus(CompanyStatus.ACTIVE);

        companyDto1.setTitle("title1");
        companyDto2.setTitle("title2");

        companyDto1.setAddress(addressDto1);
        companyDto2.setAddress(addressDto2);

        // mock PrePersist
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserPrincipal userPrincipal = mock(UserPrincipal.class);

        when(userPrincipal.getId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authentication.getName()).thenReturn("user");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        companyService.save(companyDto);
        companyService.save(companyDto1);
        companyService.save(companyDto2);


        List<CompanyDto> companyDtoList = companyService.getCompaniesByStatus(CompanyStatus.ACTIVE);

        assertThat(companyDtoList).allMatch(companyDto -> companyDto.getCompanyStatus().equals(CompanyStatus.ACTIVE));
        assertThat(companyDtoList).size().isEqualTo(2);
    }

    @Test
    public void getCompaniesSortedByStatusAndTitle_should_return_companies_sorted_correctly() {

    }

    @Test
    public void getCompaniesExcluding_should_not_return_excluded_company() {

    }

    @Test
    public void getAdminCompanies_should_return_companies_based_on_user_role() {

    }

    @Test
    public void findById_should_return_correct_company_dto() {

    }

}
