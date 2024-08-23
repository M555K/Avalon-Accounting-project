package com.company.service.unit;

import com.company.dto.AddressDto;
import com.company.dto.ClientVendorDto;
import com.company.dto.CompanyDto;
import com.company.entity.Address;
import com.company.entity.ClientVendor;
import com.company.entity.Company;
import com.company.enums.ClientVendorType;
import com.company.exception.ClientVendorNotFoundException;
import com.company.repository.ClientVendorRepository;
import com.company.service.*;
import com.company.service.impl.ClientVendorServiceImpl;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientVendorServiceImplTest {

    @Mock
    private ClientVendorRepository clientVendorRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private AddressService addressService;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private ClientVendorServiceImpl clientVendorService;

    private ClientVendor clientVendor;
    private ClientVendorDto clientVendorDto;
    private CompanyDto companyDto;
    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setId(1L);

        clientVendor = new ClientVendor();
        clientVendor.setId(1L);
        clientVendor.setAddress(address);

        clientVendorDto = new ClientVendorDto();
        clientVendorDto.setId(1L);
        clientVendorDto.setAddress(new AddressDto());

        companyDto = new CompanyDto();
        companyDto.setId(1L);
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
        when(clientVendorRepository.findClientVendorsByCompanyId(anyLong(), any(ClientVendorType.class))).thenReturn(Collections.singletonList(clientVendor));
        when(mapperUtil.convert(any(ClientVendor.class), any(ClientVendorDto.class))).thenReturn(clientVendorDto);

        List<ClientVendorDto> clientVendorDtos = clientVendorService.findClientVendorsByCompanyId(ClientVendorType.CLIENT);

        assertNotNull(clientVendorDtos);
        assertEquals(1, clientVendorDtos.size());
    }

    @Test
    void findClientVendorsByCompanyIdAndClientVendorType_ShouldReturnListOfClientVendorDtos() {
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(clientVendorRepository.findClientVendorsByCompanyIdAndClientVendorType(anyLong())).thenReturn(Collections.singletonList(clientVendor));
        when(mapperUtil.convert(any(ClientVendor.class), any(ClientVendorDto.class))).thenReturn(clientVendorDto);
        when(invoiceService.hasInvoicesByClientVendorId(anyLong())).thenReturn(true);

        List<ClientVendorDto> clientVendorDtos = clientVendorService.findClientVendorsByCompanyIdAndClientVendorType();

        assertNotNull(clientVendorDtos);
        assertEquals(1, clientVendorDtos.size());
        assertTrue(clientVendorDtos.get(0).isHasInvoice());
    }

    @Test
    void saveClientVendor_ShouldReturnSavedClientVendorDto() {
        when(addressService.saveAndRetrieveId(any())).thenReturn(1L);
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(mapperUtil.convert(any(ClientVendorDto.class), any(ClientVendor.class))).thenReturn(clientVendor);
        when(mapperUtil.convert(any(CompanyDto.class), any(Company.class))).thenReturn(new Company());
        when(addressService.findById(anyLong())).thenReturn(null);
        when(clientVendorRepository.save(any(ClientVendor.class))).thenReturn(clientVendor);
        when(mapperUtil.convert(any(ClientVendor.class), any(ClientVendorDto.class))).thenReturn(clientVendorDto);

        ClientVendorDto savedClientVendorDto = clientVendorService.saveClientVendor(clientVendorDto);

        assertNotNull(savedClientVendorDto);
        assertEquals(1L, savedClientVendorDto.getId());
    }

    @Test
    void updateClientVendor_ShouldReturnUpdatedClientVendorDto() {
        ClientVendor updatedClientVendor = new ClientVendor();
        Company company = new Company();
        Address address = new Address();
        address.setId(1L);
        AddressDto addressDto = new AddressDto();
        addressDto.setId(1L);

        clientVendorDto.setAddress(addressDto);

        // Mocking the repository call
        when(clientVendorRepository.findById(1L)).thenReturn(Optional.of(clientVendor));
        // Mocking the address service call
        when(addressService.saveAndRetrieveId(any(AddressDto.class))).thenReturn(1L);
        // Mocking the mapper util conversion
        when(mapperUtil.convert(any(ClientVendorDto.class), any(ClientVendor.class))).thenReturn(updatedClientVendor);
        when(mapperUtil.convert(any(CompanyDto.class), any(Company.class))).thenReturn(company);
        when(mapperUtil.convert(any(ClientVendor.class), any(ClientVendorDto.class))).thenReturn(clientVendorDto);
        when(addressService.findById(1L)).thenReturn(address);
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);

        ClientVendorDto updatedClientVendorDto = clientVendorService.updateClientVendor(clientVendorDto);

        assertNotNull(updatedClientVendorDto);
        assertEquals(clientVendorDto.getId(), updatedClientVendorDto.getId());
        assertEquals(addressDto.getId(), updatedClientVendorDto.getAddress().getId());

        verify(clientVendorRepository).save(any(ClientVendor.class));
    }

    @Test
    void deleteClientVendor_ShouldSetIsDeletedToTrue() {
        when(clientVendorRepository.findById(anyLong())).thenReturn(Optional.of(clientVendor));

        clientVendorService.deleteClientVendor(1L);

        assertTrue(clientVendor.getIsDeleted());
        verify(clientVendorRepository, times(1)).save(clientVendor);
    }
}