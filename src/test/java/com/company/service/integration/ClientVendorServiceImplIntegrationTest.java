package com.company.service.integration;

import com.company.dto.AddressDto;
import com.company.dto.ClientVendorDto;
import com.company.dto.CompanyDto;
import com.company.entity.Address;
import com.company.entity.ClientVendor;
import com.company.entity.Company;
import com.company.enums.ClientVendorType;
import com.company.exception.ClientVendorNotFoundException;
import com.company.repository.ClientVendorRepository;
import com.company.service.AddressService;
import com.company.service.ClientVendorService;
import com.company.service.CompanyService;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ClientVendorServiceImplIntegrationTest {

    @Autowired
    private ClientVendorService clientVendorService;

    @MockBean
    private ClientVendorRepository clientVendorRepository;

    @MockBean
    private AddressService addressService;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private MapperUtil mapperUtil;

    @Autowired
    private Validator validator;

    private ClientVendor clientVendor;
    private ClientVendorDto clientVendorDto;
    private CompanyDto companyDto;
    private Address address;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setId(1L);

        clientVendor = new ClientVendor();
        clientVendor.setId(1L);
        clientVendor.setAddress(address);

        clientVendorDto = new ClientVendorDto();
        clientVendorDto.setId(1L);
        clientVendorDto.setClientVendorName("Valid Name");
        clientVendorDto.setPhone("+1234567890");
        clientVendorDto.setWebsite("https://valid.website.com");
        clientVendorDto.setClientVendorType(ClientVendorType.CLIENT);

        addressDto = new AddressDto();
        addressDto.setId(1L);
        addressDto.setAddressLine1("123 Main St");
        addressDto.setCity("City");
        addressDto.setState("State");
        addressDto.setCountry("Country");
        addressDto.setZipCode("12345-6789");
        clientVendorDto.setAddress(addressDto);

        companyDto = new CompanyDto();
        companyDto.setId(1L);
    }


    @Test
    void updateClientVendor_ShouldReturnUpdatedClientVendorDto() {
        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.of(clientVendor));
        when(addressService.saveAndRetrieveId(any(AddressDto.class))).thenReturn(1L);
        when(mapperUtil.convert(any(ClientVendorDto.class), any(ClientVendor.class))).thenReturn(clientVendor);
        when(mapperUtil.convert(any(CompanyDto.class), any(Company.class))).thenReturn(new Company());
        when(mapperUtil.convert(any(ClientVendor.class), any(ClientVendorDto.class))).thenReturn(clientVendorDto);
        when(addressService.findById(anyLong())).thenReturn(address);
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);

        ClientVendorDto updatedClientVendorDto = clientVendorService.updateClientVendor(clientVendorDto);

        assertNotNull(updatedClientVendorDto);
        assertEquals(clientVendorDto.getId(), updatedClientVendorDto.getId());
        assertEquals(addressDto.getId(), updatedClientVendorDto.getAddress().getId());

        verify(clientVendorRepository).save(any(ClientVendor.class));
    }

    @Test
    void findById_ShouldReturnClientVendorDto_WhenClientVendorExists() {
        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.of(clientVendor));
        when(mapperUtil.convert(any(ClientVendor.class), any(ClientVendorDto.class))).thenReturn(clientVendorDto);

        ClientVendorDto foundClientVendor = clientVendorService.findById(1L);

        assertNotNull(foundClientVendor);
        assertEquals(1L, foundClientVendor.getId());
    }

    @Test
    void findById_ShouldThrowException_WhenClientVendorDoesNotExist() {
        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ClientVendorNotFoundException.class, () -> clientVendorService.findById(1L));
    }

    @Test
    void findClientVendorsByCompanyId_ShouldReturnListOfClientVendorDtos() {
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(clientVendorRepository.findClientVendorsByCompanyId(anyLong(), any(ClientVendorType.class)))
                .thenReturn(Collections.singletonList(clientVendor));
        when(mapperUtil.convert(any(ClientVendor.class), any(ClientVendorDto.class))).thenReturn(clientVendorDto);

        var clientVendorDtos = clientVendorService.findClientVendorsByCompanyId(ClientVendorType.CLIENT);

        assertNotNull(clientVendorDtos);
        assertEquals(1, clientVendorDtos.size());
    }

    @Test
    void saveClientVendor_ShouldReturnSavedClientVendorDto() {
        when(addressService.saveAndRetrieveId(any())).thenReturn(1L);
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(mapperUtil.convert(any(ClientVendorDto.class), any(ClientVendor.class))).thenReturn(clientVendor);
        when(mapperUtil.convert(any(CompanyDto.class), any(Company.class))).thenReturn(new Company());
        when(addressService.findById(anyLong())).thenReturn(address);
        when(clientVendorRepository.save(any(ClientVendor.class))).thenReturn(clientVendor);
        when(mapperUtil.convert(any(ClientVendor.class), any(ClientVendorDto.class))).thenReturn(clientVendorDto);

        ClientVendorDto savedClientVendorDto = clientVendorService.saveClientVendor(clientVendorDto);

        assertNotNull(savedClientVendorDto);
        assertEquals(1L, savedClientVendorDto.getId());
    }

    @Test
    void deleteClientVendor_ShouldSetIsDeletedToTrue() {
        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.of(clientVendor));

        clientVendorService.deleteClientVendor(1L);

        assertTrue(clientVendor.getIsDeleted());
        verify(clientVendorRepository, times(1)).save(clientVendor);
    }

    // Validation Tests
    @Test
    void saveClientVendor_ShouldThrowException_WhenClientVendorNameIsBlank() {
        clientVendorDto.setClientVendorName("");

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.saveClientVendor(clientVendorDto);
        });
    }

    @Test
    void saveClientVendor_ShouldThrowException_WhenClientVendorTypeIsNull() {
        clientVendorDto.setClientVendorType(null);

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.saveClientVendor(clientVendorDto);
        });
    }

    @Test
    void saveClientVendor_ShouldThrowException_WhenAddressFieldsAreInvalid() {
        clientVendorDto.getAddress().setAddressLine1("");

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.saveClientVendor(clientVendorDto);
        });
    }

    @Test
    void saveClientVendor_ShouldThrowException_WhenAddressIsNull() {
        clientVendorDto.setAddress(null);

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.saveClientVendor(clientVendorDto);
        });
    }

    @Test
    void updateClientVendor_ShouldThrowException_WhenClientVendorNameIsBlank() {
        clientVendorDto.setClientVendorName("");

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.updateClientVendor(clientVendorDto);
        });
    }

    @Test
    void updateClientVendor_ShouldThrowException_WhenClientVendorTypeIsNull() {
        clientVendorDto.setClientVendorType(null);

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.updateClientVendor(clientVendorDto);
        });
    }

    @Test
    void updateClientVendor_ShouldThrowException_WhenAddressFieldsAreInvalid() {
        clientVendorDto.getAddress().setAddressLine1("");

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.updateClientVendor(clientVendorDto);
        });
    }

    @Test
    void updateClientVendor_ShouldThrowException_WhenAddressIsNull() {
        clientVendorDto.setAddress(null);

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.updateClientVendor(clientVendorDto);
        });
    }

    private void validateClientVendorDto(ClientVendorDto clientVendorDto) {
        var violations = validator.validate(clientVendorDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    @Test
    void saveClientVendor_ShouldThrowException_WhenClientVendorNameIsDuplicate() {
        when(clientVendorRepository.save(any(ClientVendor.class))).thenThrow(new DataIntegrityViolationException("Duplicate name"));
        when(mapperUtil.convert(any(ClientVendorDto.class), any(ClientVendor.class))).thenReturn(clientVendor);

        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            clientVendorService.saveClientVendor(clientVendorDto);
        });

        assertEquals("Duplicate name", exception.getMessage());
    }

    @Test
    void saveClientVendor_ShouldThrowException_WhenClientVendorNameExceedsMaxLength() {
        clientVendorDto.setClientVendorName("a".repeat(51)); // Assuming max length is 50

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.saveClientVendor(clientVendorDto);
        });
    }

    @Test
    void saveClientVendor_ShouldThrowException_WhenPhoneIsInvalid() {
        clientVendorDto.setPhone("invalid-phone");

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.saveClientVendor(clientVendorDto);
        });
    }

    @Test
    void saveClientVendor_ShouldThrowException_WhenWebsiteIsInvalid() {
        clientVendorDto.setWebsite("invalid-website");

        assertThrows(ConstraintViolationException.class, () -> {
            validateClientVendorDto(clientVendorDto);
            clientVendorService.saveClientVendor(clientVendorDto);
        });
    }

}