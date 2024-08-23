package com.company.service.integration;

import com.company.dto.ClientVendorDto;
import com.company.dto.InvoiceDto;
import com.company.entity.Invoice;
import com.company.entity.User;
import com.company.entity.common.UserPrincipal;
import com.company.enums.InvoiceType;
import com.company.exception.InvoiceNotFoundException;
import com.company.repository.InvoiceProductRepository;
import com.company.repository.InvoiceRepository;
import com.company.service.CompanyService;
import com.company.service.InvoiceProductService;
import com.company.service.InvoiceService;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InvoiceServiceIntegrationTest {
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private InvoiceProductService invoiceProductService;

    @Autowired
    private InvoiceProductRepository invoiceProductRepository;

    @Autowired
    private MapperUtil mapperUtil;
    @BeforeEach
    void setUp(){
        User user = new User();
        user.setId(1L);
        user.setUsername("manager@greentech.com");
        UserPrincipal userPrincipal = new UserPrincipal(user);

        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(userPrincipal, null);

        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }
    @Test
    public void should_save_invoice() {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceNo("S-001");

        Invoice savedInvoice = invoiceService.save(invoiceDto, InvoiceType.SALES);

        Invoice invoiceFromDb =  invoiceRepository.findById(savedInvoice.getId()).orElse(null);

        assertNotNull(invoiceFromDb);
        assertEquals(invoiceDto.getInvoiceNo(), invoiceFromDb.getInvoiceNo());
        assertEquals(invoiceDto.getInvoiceType(), invoiceFromDb.getInvoiceType());
    }

    @Test
    public void should_find_invoice_by_id_or_throw_exception_if_invoice_not_found() {
        Long id = 1L;
        Long exceptionId = 55L;
        Invoice foundInvoice = invoiceRepository.findById(id).orElse(null);

        InvoiceDto invoiceDto = invoiceService.findById(id);
        assertNotNull(foundInvoice);
        assertEquals(invoiceDto.getInvoiceNo(), foundInvoice.getInvoiceNo());
        assertEquals(InvoiceType.PURCHASE, foundInvoice.getInvoiceType());
        assertEquals("P-001",foundInvoice.getInvoiceNo());
        assertNotNull(invoiceDto);
        Throwable invoiceNotFound = assertThrows(InvoiceNotFoundException.class, () -> invoiceService.findById(exceptionId));
        assertEquals("No Invoice Found!", invoiceNotFound.getMessage());
    }

    @Test
    public void should_update_invoice() {
//        Long id = 1L;
//        Invoice invoiceFromDb =  invoiceRepository.findById(id).orElseThrow();
//
//        InvoiceDto invoiceToBeUpdated = invoiceService.findById(id);
//
//        ClientVendorDto clientVendorDto = new ClientVendorDto();
//        clientVendorDto.setClientVendorName("New Vendor");
//
//        invoiceToBeUpdated.setClientVendor(clientVendorDto);
//        invoiceToBeUpdated.setInvoiceStatus(invoiceFromDb.getInvoiceStatus());
//        invoiceToBeUpdated.setInvoiceType(invoiceFromDb.getInvoiceType());
//        invoiceToBeUpdated.setInvoiceNo(invoiceFromDb.getInvoiceNo());
//
//
//        invoiceService.update(invoiceToBeUpdated, InvoiceType.PURCHASE);
//
//        Invoice invoiceFromDbUpdated = invoiceRepository.findById(id).orElseThrow();
//
//       assertNotNull(invoiceFromDbUpdated);
//       assertEquals(invoiceFromDbUpdated.getInvoiceStatus(),invoiceToBeUpdated.getInvoiceStatus());
//       assertEquals(invoiceFromDbUpdated.getInvoiceType(),invoiceToBeUpdated.getInvoiceType());
//       assertEquals(invoiceFromDbUpdated.getInvoiceNo(),invoiceToBeUpdated.getInvoiceNo());
//       assertEquals("New Vendor",invoiceFromDbUpdated.getClientVendor().getClientVendorName());

        Long id = 1L;

        // Fetch the invoice from the database
        Invoice invoiceFromDb = invoiceRepository.findById(id).orElseThrow();

        // Prepare the DTO for the update
        InvoiceDto invoiceToBeUpdated = invoiceService.findById(id);
        ClientVendorDto clientVendorDto = new ClientVendorDto();
        clientVendorDto.setClientVendorName("Photobug Tech");

        // Update the necessary fields
        invoiceToBeUpdated.setClientVendor(clientVendorDto);
        invoiceToBeUpdated.setInvoiceStatus(invoiceFromDb.getInvoiceStatus());
        invoiceToBeUpdated.setInvoiceType(invoiceFromDb.getInvoiceType());
        invoiceToBeUpdated.setInvoiceNo(invoiceFromDb.getInvoiceNo());

        // Perform the update through the service
        invoiceService.update(invoiceToBeUpdated, InvoiceType.PURCHASE);

        // Fetch the updated invoice from the database
        Invoice invoiceFromDbUpdated = invoiceRepository.findById(id).orElseThrow();

        // Perform the necessary assertions
        assertNotNull(invoiceFromDbUpdated);
        assertEquals(invoiceFromDbUpdated.getInvoiceStatus(), invoiceToBeUpdated.getInvoiceStatus());
        assertEquals(invoiceFromDbUpdated.getInvoiceType(), invoiceToBeUpdated.getInvoiceType());
        assertEquals(invoiceFromDbUpdated.getInvoiceNo(), invoiceToBeUpdated.getInvoiceNo());
        assertEquals("Photobug Tech", invoiceFromDbUpdated.getClientVendor().getClientVendorName());
    }


    @Test
    public void should_delete_invoice(){

    }

}
