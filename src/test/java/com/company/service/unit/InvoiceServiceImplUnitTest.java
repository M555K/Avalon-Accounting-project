package com.company.service.unit;

import com.company.dto.CompanyDto;
import com.company.dto.InvoiceDto;
import com.company.dto.InvoiceProductDto;
import com.company.dto.ProductDto;
import com.company.entity.Invoice;
import com.company.entity.InvoiceProduct;
import com.company.entity.Product;
import com.company.enums.InvoiceStatus;
import com.company.enums.InvoiceType;
import com.company.exception.InvoiceNotFoundException;
import com.company.exception.InvoiceProductNotFoundException;
import com.company.repository.InvoiceProductRepository;
import com.company.repository.InvoiceRepository;
import com.company.service.CompanyService;
import com.company.service.InvoiceProductService;
import com.company.service.ProductService;
import com.company.service.impl.InvoiceServiceImpl;
import com.company.util.MapperUtil;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceImplUnitTest {
    @Spy
    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private CompanyService companyService;

    @Mock
    private InvoiceProductService invoiceProductService;

    @Mock
    private InvoiceProductRepository invoiceProductRepository;

    @Mock
    private ProductService productService;

    Invoice invoice;
    InvoiceDto invoiceDto;

    @Before("")

    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void should_throw_exception_when_invoice_not_found() {
        when(invoiceRepository.findInvoiceById(anyLong())).thenReturn(Optional.empty());// from repo return Optional
        Throwable invoiceNotFound = assertThrows(InvoiceNotFoundException.class, () -> invoiceService.findById(1L));
        assertEquals("No Invoice Found!", invoiceNotFound.getMessage());
        verify(invoiceRepository, times(1)).findInvoiceById(1L);

    }

    @Test
    public void should_find_invoice() {
        Long id = 1L;
        BigDecimal price = new BigDecimal("100.00");
        BigDecimal totalTax = new BigDecimal("20.00");
        when(invoiceRepository.findInvoiceById(id)).thenReturn(Optional.of(invoice));
        when(mapperUtil.convert(any(Invoice.class), any(InvoiceDto.class))).thenReturn(invoiceDto);

        doReturn(price).when(invoiceService).calculatePriceByInvoiceId(id);
        doReturn(totalTax).when(invoiceService).calculateTotalTaxByInvoiceId(id);
        InvoiceDto result = invoiceService.findById(id);

        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(id);
        AssertionsForClassTypes.assertThat(result.getInvoiceNo()).isEqualTo("S-001");
        assertThat(result.getInvoiceType()).isEqualTo(InvoiceType.SALES);
        AssertionsForClassTypes.assertThat(result.getPrice()).isEqualTo(price);
        AssertionsForClassTypes.assertThat(result.getTax()).isEqualTo(totalTax);
        AssertionsForClassTypes.assertThat(result.getTotal()).isEqualTo(price.add(totalTax));

        verify(invoiceRepository).findInvoiceById(id);

    }

    @Test
    public void should_list_all_invoices_by_type() {
        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(1L);
        Invoice invoice1 = new Invoice();
        invoice1.setId(1L);
        invoice1.setInvoiceType(InvoiceType.SALES);
        invoice1.setInvoiceNo("S-001");
        Invoice invoice2 = new Invoice();
        invoice2.setId(2L);
        invoice2.setInvoiceNo("S-002");
        invoice2.setInvoiceType(InvoiceType.SALES);
        List<Invoice> invoiceList = Arrays.asList(invoice1,invoice2);
  
        InvoiceDto invoiceDto1 = new InvoiceDto();
        invoiceDto1.setId(1L);
        invoiceDto1.setInvoiceNo("S-001");
        invoiceDto1.setInvoiceType(InvoiceType.SALES);
        InvoiceDto invoiceDto2 = new InvoiceDto();
        invoiceDto2.setId(2L);
        invoiceDto2.setInvoiceNo("S-002");
        invoiceDto2.setInvoiceType(InvoiceType.SALES);
        List<InvoiceDto> invoiceDtoList = Arrays.asList(invoiceDto1,invoiceDto2);

        // Mock method calls
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(invoiceRepository.listAllByInvoiceTypeAndCompanyId(InvoiceType.SALES,companyDto.getId())).thenReturn(invoiceList);

        doReturn(invoiceDto1).when(mapperUtil).convert(eq(invoice1), any(InvoiceDto.class));
        doReturn(invoiceDto2).when(mapperUtil).convert(eq(invoice2), any(InvoiceDto.class));

        List<InvoiceDto> actualList = invoiceService.listAllInvoicesByType(InvoiceType.SALES);
        assertThat(actualList).isNotNull();
        assertThat(actualList.size()).isEqualTo(2);


        assertEquals(actualList.get(0).getInvoiceNo(), invoiceDto1.getInvoiceNo());
        assertEquals(actualList.get(1).getInvoiceNo(), invoiceDto2.getInvoiceNo());
        assertThat(actualList).usingRecursiveComparison().isEqualTo(invoiceDtoList);

    }

    @Test
    public void should_save_invoice(){
        Invoice invoice1 = new Invoice();
        InvoiceDto invoiceDto1 = new InvoiceDto();
        invoiceDto1.setId(1L);
        invoiceDto1.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        invoiceDto1.setInvoiceType(InvoiceType.SALES);
        when(mapperUtil.convert(invoiceDto1, new Invoice())).thenReturn(invoice1);
        when(invoiceRepository.save(invoice1)).thenReturn(invoice1);

        Invoice dummy = invoiceService.save(invoiceDto1,InvoiceType.SALES);

        verify(mapperUtil, times(1)).convert(invoiceDto1, new Invoice());
        verify(invoiceRepository, times(1)).save(invoice1);

        assertNotNull(dummy);
        assertEquals(invoice1,dummy);
        assertEquals(InvoiceType.SALES, invoiceDto1.getInvoiceType());


    }

    @Test
    public void should_generate_invoice_number(){
        Long companyId = 1L;
        get_the_company(companyId);
        String invoiceNo = "S-001";

        when(invoiceRepository.findLatestInvoiceNumber(companyId,InvoiceType.SALES)).thenReturn(invoiceNo);

        // perform from service
        String generateInvoiceNumber = invoiceService.generateNextInvoiceNo(InvoiceType.SALES);
        assertNotNull(generateInvoiceNumber);
        assertEquals("S-002",generateInvoiceNumber);
        // interactions
        verify(invoiceRepository,times(1)).findLatestInvoiceNumber(companyId,InvoiceType.SALES);

    }
    @Test
    public void should_create_new_invoice(){
        Long companyId = 1L;
        get_the_company(companyId);
        String expectedInvoiceNo = "S-001";

        when(invoiceRepository.findLatestInvoiceNumber(companyId,InvoiceType.SALES)).thenReturn(expectedInvoiceNo);

        InvoiceDto newInvoiceDto = invoiceService.createNewInvoice(InvoiceType.SALES);

        assertNotNull(newInvoiceDto);
        assertEquals("S-002",newInvoiceDto.getInvoiceNo());

    }

    @Test
    public void should_update_invoice(){
        Long companyId = 1L;
        get_the_company(companyId);
        Invoice invoiceInDB = new Invoice();
        invoiceInDB.setId(1L);
        InvoiceDto invoiceDto1 = new InvoiceDto();
        invoiceDto1.setId(1L);
        invoiceDto1.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);
        when(invoiceRepository.findInvoiceById(invoiceDto1.getId())).thenReturn(Optional.of(invoiceInDB));

        Invoice invoiceToBeUpdated = new Invoice();
        when(mapperUtil.convert(invoiceDto1, new Invoice())).thenReturn(invoiceToBeUpdated);
        invoiceService.update(invoiceDto1,InvoiceType.SALES);


        verify(invoiceRepository, times(1)).findInvoiceById(1L);
        verify(invoiceRepository, times(1)).save(invoiceToBeUpdated);
    }

    @Test
    public void should_delete_invoice(){
        Long idToDelete= 1L;
        Invoice invoiceInDB = new Invoice();
        invoiceInDB.setId(idToDelete);
        invoiceInDB.setIsDeleted(false);

        when(invoiceRepository.findById(idToDelete)).thenReturn(Optional.of(invoiceInDB));
        when(invoiceProductService.listAllByInvoiceId(idToDelete)).thenReturn(Collections.emptyList());

        invoiceService.delete(idToDelete);
        verify(invoiceRepository, times(1)).findById(1L);
        verify(invoiceRepository,times(1)).save(invoiceInDB);

        verify(invoiceProductService,times(1)).listAllByInvoiceId(idToDelete);
        verify(invoiceProductService,times(0)).delete(idToDelete);

        assertTrue(invoiceInDB.getIsDeleted());
    }

    @Test
    public void should_approve_invoice_if_quantity_of_product_is_enough() {
        Long idToApproveInvoice = 1L;

        // Mock data
        Invoice invoice = new Invoice();
        invoice.setId(idToApproveInvoice);
        invoice.setInvoiceStatus(InvoiceStatus.AWAITING_APPROVAL);

        Product product = new Product();
        product.setId(idToApproveInvoice);
        product.setQuantityInStock(10); // Ensure enough stock for approval

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setQuantityInStock(10);

        BigDecimal price =  BigDecimal.TEN;

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(idToApproveInvoice);
        invoiceProductDto.setQuantity(5);
        invoiceProductDto.setRemainingQuantity(5);
        invoiceProductDto.setProduct(productDto);
        invoiceProductDto.setPrice(price);

        InvoiceProduct invoiceProductFromDb = new InvoiceProduct();
        invoiceProductFromDb.setProduct(product);
        invoiceProductFromDb.setId(idToApproveInvoice);

        // Mocking repository methods
        when(invoiceRepository.findById(idToApproveInvoice)).thenReturn(Optional.of(invoice));
        when(invoiceProductService.listAllByInvoiceId(idToApproveInvoice)).thenReturn(Collections.singletonList(invoiceProductDto));
        doNothing().when(productService).save(any(ProductDto.class));

        // Perform action
        invoiceService.approve(idToApproveInvoice);

        // Verify interactions
        verify(invoiceRepository).findById(idToApproveInvoice);
        verify(invoiceProductService, times(2)).listAllByInvoiceId(idToApproveInvoice);
        verify(invoiceProductService).save(invoiceProductDto, idToApproveInvoice);
        verify(invoiceRepository, times(1)).save(invoice);

        // Assert final state
        assertEquals(InvoiceStatus.APPROVED, invoice.getInvoiceStatus());
    }

    @Test
    public void should_throw_exception_when_invoice_is_not_found_for_approval_procedure(){
        Long idToApproveInvoice = 1L;
        when(invoiceRepository.findById(idToApproveInvoice)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(InvoiceNotFoundException.class,()->invoiceService.approve(idToApproveInvoice));
        assertEquals("No Invoice Found with id -"+idToApproveInvoice, exception.getMessage());
    }

    @Test
    public void should_throw_exception_when_not_enough_product_stock_for_approval_procedure(){
        Long idToApproveInvoice = 1L;
        when(invoiceRepository.findById(idToApproveInvoice)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(InvoiceNotFoundException.class,()->invoiceService.approve(idToApproveInvoice));
        assertEquals("No Invoice Found with id -"+idToApproveInvoice, exception.getMessage());
    }
    
    @Test
    public void check_if_invoice_can_be_approved_whens_stock_is_sufficient() {
        // Arrange
        Long productId = 1L;
        int quantityRequired = 5;
        int quantityInStock = 10;

        // Create a mock InvoiceProductDto with the required attributes
        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(productId);
        invoiceProductDto.setQuantity(quantityRequired);

        // Create a mock InvoiceProduct with the required attributes
        InvoiceProduct invoiceProduct = new InvoiceProduct();
        invoiceProduct.setId(productId);
        Product product = new Product();
        product.setQuantityInStock(quantityInStock);
        invoiceProduct.setProduct(product);

        // Mock the repository method
        when(invoiceProductRepository.findById(productId)).thenReturn(Optional.of(invoiceProduct));

        // Act
    //    boolean canApprove = invoiceService.checkIfInvoiceCanBeApproved(invoiceProductDto);

        // Assert
      //  assertTrue(canApprove);
    }

    @Test
    public void _check_if_invoice_can_be_approved_when_stock_is_insufficient() {
        Long productId = 1L;
        int quantityRequired = 10;
        int quantityInStock = 5;


        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(productId);
        invoiceProductDto.setQuantity(quantityRequired);

        InvoiceProduct invoiceProduct = new InvoiceProduct();
        invoiceProduct.setId(productId);
        Product product = new Product();
        product.setQuantityInStock(quantityInStock);
        invoiceProduct.setProduct(product);

        when(invoiceProductRepository.findById(productId)).thenReturn(Optional.of(invoiceProduct));

        // Act
        //canApprove = invoiceService.checkIfInvoiceCanBeApproved(invoiceProductDto);

        // Assert
     //   assertFalse(canApprove, "Not enough stock to approve the invoice");
    }

    @Test
    public void check_if_invoice_can_be_approved_when_invoice_product_not_found() {
        // Arrange
        Long productId = 1L;
        int quantityRequired = 5;

        // Create a mock InvoiceProductDto with the required attributes
        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setId(productId);
        invoiceProductDto.setQuantity(quantityRequired);

        // Mock the repository method to return an empty Optional
        when(invoiceProductRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvoiceProductNotFoundException.class, () -> {
            invoiceService.checkIfInvoiceCanBeApproved(invoiceProductDto);
        }, "InvoiceProductNotFoundException thrown");
    }

    @Test
    public void should_show_last_three_approved_invoices(){
        Long companyId = 1L;
        InvoiceStatus status = InvoiceStatus.APPROVED;

        // Mocking the companyService to return a mock company with an ID
        CompanyDto mockCompanyDto = new CompanyDto();
        mockCompanyDto.setId(companyId);
        when(companyService.getCompanyByLoggedInUser()).thenReturn(mockCompanyDto);
// Mocking the invoiceRepository to return a list of invoices
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();
        Invoice invoice3 = new Invoice();
        List<Invoice> mockInvoices = Arrays.asList(invoice1, invoice2, invoice3);
        when(invoiceRepository.findTop3ByCompanyIdAndInvoiceStatusOrderByDateDesc(companyId, status))
                .thenReturn(mockInvoices);

        // Mocking the mapperUtil to convert Invoice to InvoiceDto
        InvoiceDto mockInvoiceDto1 = new InvoiceDto();
        InvoiceDto mockInvoiceDto2 = new InvoiceDto();
        InvoiceDto mockInvoiceDto3 = new InvoiceDto();
        // Mocking price and tax calculations
        when(invoiceService.calculatePriceByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(100));
        when(invoiceService.calculateTotalTaxByInvoiceId(anyLong())).thenReturn(BigDecimal.valueOf(10));

        // Act
        List<InvoiceDto> result = invoiceService.showLastThreeApprovedInvoices(status);

        // Assert
        assertEquals(3, result.size());
        for (InvoiceDto invoiceDto : result) {
            assertEquals(BigDecimal.valueOf(100), invoiceDto.getPrice());
            assertEquals(BigDecimal.valueOf(10).setScale(2), invoiceDto.getTax());
            assertEquals(BigDecimal.valueOf(110).setScale(2), invoiceDto.getTotal());
        }

        // Verify interactions
        verify(companyService).getCompanyByLoggedInUser();
        verify(invoiceRepository).findTop3ByCompanyIdAndInvoiceStatusOrderByDateDesc(companyId, status);
        verify(mapperUtil, times(3)).convert(any(Invoice.class), eq(InvoiceDto.class));
        verify(invoiceService, times(3)).calculatePriceByInvoiceId(anyLong());
        verify(invoiceService, times(3)).calculateTotalTaxByInvoiceId(anyLong());
    }
    private void get_the_company(Long companyId){
        CompanyDto company = new CompanyDto();
        company.setId(companyId);
        when(companyService.getCompanyByLoggedInUser()).thenReturn(company);
    }
}

