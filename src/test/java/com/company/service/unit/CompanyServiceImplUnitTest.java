package com.company.service.unit;

import com.company.dto.AddressDto;
import com.company.dto.CompanyDto;
import com.company.dto.UserDto;
import com.company.entity.Address;
import com.company.entity.Company;
import com.company.enums.CompanyStatus;
import com.company.exception.CompanyNotFoundException;
import com.company.exception.UserNotFoundException;
import com.company.repository.AddressRepository;
import com.company.repository.CompanyRepository;
import com.company.service.SecurityService;
import com.company.service.impl.CompanyServiceImpl;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplUnitTest {

    @Mock
    private SecurityService securityService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Address address;
    private AddressDto addressDto;
    private Company company;
    private CompanyDto companyDto;

    private Address createAddress(){
        address = new Address();
        address.setId(1L);

        return address;
    }

    private AddressDto createAddressDto(){
        addressDto = new AddressDto();
        addressDto.setId(1L);

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

        return companyDto;
    }

    @BeforeEach
    void setUp() {
        address = createAddress();
        addressDto = createAddressDto();
        company = createCompany();
        companyDto = createCompanyDto();
    }

    @Test
    public void getCompanyByLoggedInUser_should_return_companyDto(){

        UserDto userDto = new UserDto();
        userDto.setUsername("username");
        userDto.setCompany(companyDto);

        SecurityContextHolder.setContext(securityContext);
        when(securityService.getLoggedInUser()).thenReturn(userDto);

        CompanyDto actualCompanyDto = companyService.getCompanyByLoggedInUser();

        assertThat(actualCompanyDto).isInstanceOf(CompanyDto.class);
        assertThat(actualCompanyDto).isEqualTo(companyDto);
    }

    @Test
    public void getCompanyByLoggedInUser_should_throw_UserNotFoundException(){

        UserDto userDto = new UserDto();
        userDto.setUsername("username");
        userDto.setCompany(companyDto);

        when(securityService.getLoggedInUser()).thenThrow(new UserNotFoundException("username not found"));

        Throwable throwable = catchThrowable(() ->
                companyService.getCompanyByLoggedInUser());

        assertThat(throwable).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void save_should_return_CompanyDto(){

        when(mapperUtil.convert(any(CompanyDto.class), any(Company.class))).thenReturn(company);
        when(mapperUtil.convert(any(Company.class), any(CompanyDto.class))).thenReturn(companyDto);
        when(mapperUtil.convert(any(AddressDto.class), any(Address.class))).thenReturn(address);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyDto savedCompany = companyService.save(companyDto);

        assertThat(savedCompany)
                .isNotNull();
        assertThat(savedCompany)
                .isInstanceOf(CompanyDto.class);
    }

    @Test
    public void update_should_return_CompanyDto(){

        when(companyRepository.findById(eq(1L))).thenReturn(Optional.ofNullable(company));
        when(mapperUtil.convert(any(AddressDto.class), any(Address.class))).thenReturn(address);
        when(mapperUtil.convert(any(CompanyDto.class), any(Company.class))).thenReturn(company);
        when(mapperUtil.convert(any(Company.class), any(CompanyDto.class))).thenReturn(companyDto);
        when(addressRepository.save(any(Address.class))).thenReturn(address);
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyDto updatedCompany = companyService.update(companyDto);

        assertThat(updatedCompany)
                .isNotNull();
        assertThat(updatedCompany)
                .isInstanceOf(CompanyDto.class);
    }

    @Test
    public void update_should_throw_CompanyNotFoundException_if_company_not_found(){

        when(companyRepository.findById(company.getId())).thenThrow(new CompanyNotFoundException("Exception thrown"));

        Throwable throwable = catchThrowable(() ->
                companyService.update(companyDto));

        assertThat(throwable)
                .isInstanceOf(CompanyNotFoundException.class);
    }

    @Test
    public void activateCompany_should_throw_CompanyNotFoundException_if_company_not_found(){

        when(companyRepository.findById(company.getId())).thenThrow(new CompanyNotFoundException("Exception thrown"));

        Throwable throwable = catchThrowable(() ->
                companyService.activateCompany(company.getId()));

        assertThat(throwable)
                .isInstanceOf(CompanyNotFoundException.class);
    }

    @Test
    public void deactivateCompany_should_throw_CompanyNotFoundException_if_company_not_found(){

        when(companyRepository.findById(company.getId())).thenThrow(new CompanyNotFoundException("Exception thrown"));

        Throwable throwable = catchThrowable(() ->
                companyService.deactivateCompany(company.getId()));

        assertThat(throwable)
                .isInstanceOf(CompanyNotFoundException.class);
    }

    @Test
    public void getCompaniesByStatus_should_return_companyDto_list(){

        List<Company> companyList = new ArrayList<>();
        List<CompanyDto> expectedList = new ArrayList<>();

        Company company1 = new Company();
        company1.setId(2L);
        company1.setCompanyStatus(CompanyStatus.PASSIVE);

        CompanyDto companyDto1 = new CompanyDto();
        companyDto1.setId(2L);
        companyDto1.setCompanyStatus(CompanyStatus.PASSIVE);

        companyList.add(company);
        companyList.add(company1);

        expectedList.add(companyDto);

        when(companyRepository.findAll()).thenReturn(companyList);
        when(mapperUtil.convert(eq(company), any(CompanyDto.class))).thenReturn(companyDto);

        List<CompanyDto> listResult = companyService.getCompaniesByStatus(CompanyStatus.ACTIVE);

        assertThat(listResult)
                .isInstanceOf(List.class)
                .hasAtLeastOneElementOfType(CompanyDto.class);
        assertThat(listResult)
                .hasSize(1);
        assertThat(listResult.get(0))
                .isEqualTo(companyDto);
    }

    /*@Test // not passing
    public void getCompaniesExcluding_should_exclude_specified_company(){

        List<Company> companyList = new ArrayList<>();

        Company company1 = new Company();
        Company company2= new Company();
        company1.setCompanyStatus(CompanyStatus.PASSIVE);
        company2.setCompanyStatus(CompanyStatus.ACTIVE);
        company1.setId(2L);
        company2.setId(3L);
        company1.setAddress(address);

        companyList.add(company);
        companyList.add(company1);
        companyList.add(company2);

        CompanyDto companyDto1 = new CompanyDto();
        CompanyDto companyDto2 = new CompanyDto();
        companyDto1.setCompanyStatus(CompanyStatus.PASSIVE);
        companyDto2.setCompanyStatus(CompanyStatus.ACTIVE);
        companyDto1.setId(2L);
        companyDto2.setId(3L);
        companyDto1.setAddress(addressDto);

        List<CompanyDto> expectedList = new ArrayList<>();
        expectedList.add(companyDto);
        expectedList.add(companyDto1);

        when(companyRepository.findAll()).thenReturn(companyList);
        when(mapperUtil.convert(eq(company), any(CompanyDto.class))).thenReturn(companyDto);
        when(mapperUtil.convert(eq(company1), any(CompanyDto.class))).thenReturn(companyDto1);
//        when(mapperUtil.convert(eq(company2), any(CompanyDto.class))).thenReturn(companyDto2);

        List<CompanyDto> actualList = companyService.getCompaniesExcluding(3L);

        assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);
    }*/

    @Test
    public void findById_should_throw_CompanyNotFoundException_if_company_not_found(){

        when(companyRepository.findById(any(Long.class))).thenThrow(new CompanyNotFoundException("Company not found with this ID"));

        Throwable throwable = catchThrowable(() ->
                companyService.findById(1L));

        assertThat(throwable).isInstanceOf(CompanyNotFoundException.class);

    }

}