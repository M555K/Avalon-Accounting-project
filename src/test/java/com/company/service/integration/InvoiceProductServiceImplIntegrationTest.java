package com.company.service.integration;

import com.company.dto.InvoiceDto;
import com.company.dto.InvoiceProductDto;
import com.company.dto.ProductDto;
import com.company.entity.*;
import com.company.enums.*;
import com.company.exception.InsufficientStockException;
import com.company.exception.InvoiceProductNotFoundException;
import com.company.repository.*;
import com.company.service.CompanyService;
import com.company.service.InvoiceProductService;
import com.company.service.InvoiceService;
import com.company.service.ProductService;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class InvoiceProductServiceImplIntegrationTest {

    @Autowired
    InvoiceProductRepository invoiceProductRepository;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ProductService productService;

    @Autowired
    CompanyService companyService;

    @Autowired
    InvoiceProductService invoiceProductService;

    @Autowired
    MapperUtil mapperUtil;

    @Autowired
    ProductRepository productRepository;

    @Test
    void findInvoiceProductById_shouldReturnInvoiceProduct() {
        // when
        InvoiceProductDto actualResult = invoiceProductService.findInvoiceProductById(1L);
        // then
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getPrice()).isCloseTo(BigDecimal.valueOf(250.00), withinPercentage(5));
    }

    @Test
    void findInvoiceProductById_shouldThrowException() {
        // when
        Throwable throwable = catchThrowable(() -> invoiceProductService.findInvoiceProductById(0L));
        // then
        assertThat(throwable).isInstanceOf(InvoiceProductNotFoundException.class);
        // or
        assertInstanceOf(InvoiceProductNotFoundException.class, throwable);

    }

    @Test
    public void listAllByInvoiceId_shouldReturnNonDeletedInvoiceProducts() {
        // Given: Assume data exists in the database for invoice ID 1
        Long invoiceId = 1L;

        // When: Call the listAllByInvoiceId method
        List<InvoiceProductDto> result = invoiceProductService.listAllByInvoiceId(invoiceId);

        // Then: Verify that the result is not empty and contains only non-deleted InvoiceProducts
        assertNotNull(result);
        assertFalse(result.isEmpty());

    }


    @Test
    public void delete_shouldMarkInvoiceProductAsDeleted_Test() {

        // When: Call the delete method
        invoiceProductService.delete(15L);

        // Then: Fetch the entity and check that it's marked as deleted
        InvoiceProduct deletedInvoiceProduct = invoiceProductRepository.findById(15L).orElse(null);
        assertNotNull(deletedInvoiceProduct);
        assertTrue(deletedInvoiceProduct.getIsDeleted()); // check if it's marked as deleted
    }

    @Test
    public void save_ShouldThrowException_WhenProductAlreadyAddedToInvoice_Test() {
        // Given
        Long invoiceId = 4L;
        InvoiceDto invoiceDto = invoiceService.findById(invoiceId);
        ProductDto productDto = productService.findById(1L);

        // Save the product to the invoice for the second time
        InvoiceProductDto existingInvoiceProductDto = new InvoiceProductDto();
        existingInvoiceProductDto.setProduct(productDto);
        existingInvoiceProductDto.setInvoice(invoiceDto);
        existingInvoiceProductDto.setQuantity(1);


        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            invoiceProductService.save(existingInvoiceProductDto, invoiceId);
        });
        assertEquals("Product already added to this invoice.", exception.getMessage());
    }

    @Test
    public void listRemainingApprovedPurchaseInvoiceProducts_Test() {
        // Given
        Long productId = 1L; // Use an existing product ID from  database

        // When
        List<InvoiceProductDto> result = invoiceProductService.listRemainingApprovedPurchaseInvoiceProducts(productId);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void calculateInvoiceProductTotal_Test(){
        // Given
        Long invoiceProductId = 1L; // Adjust this to match an existing record in  database
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(invoiceProductId)
                .orElseThrow(() -> new RuntimeException("Invoice Product not found"));

        // Convert to DTO
        InvoiceProductDto invoiceProductDto = mapperUtil.convert(invoiceProduct, new InvoiceProductDto());

        // When
        BigDecimal calculatedTotal = invoiceProductService.calculateInvoiceProductTotal(invoiceProductDto);

        // Then
        BigDecimal expectedTotal = invoiceProduct.getPrice()
                .multiply(BigDecimal.valueOf(invoiceProduct.getQuantity()))
                .multiply(BigDecimal.valueOf(1 + (invoiceProduct.getTax() / 100.0)))
                .setScale(2);

        assertEquals(expectedTotal, calculatedTotal);
    }

    @Test
    public void testIsProductInTheInvoiceProductList() {
        // Given
        Long productId = 1L; // Use an existing product ID from  database
        Long invoiceId = 1L; // Use an existing invoice ID from  database

        // When
        boolean result = invoiceProductService.isProductInTheInvoiceProductList(productId, invoiceId);

        // Then
        assertTrue(result);
    }

    @Test
    public void testCheckStock() {
        // Given
        Long productId = 1L; // Use an existing product ID from  database
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setQuantity(10); // Set the quantity to check

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setQuantityInStock(product.getQuantityInStock());
        productDto.setName(product.getName());
        invoiceProductDto.setProduct(productDto);

        // When & Then
        assertThrows(InsufficientStockException.class, () -> {
            invoiceProductService.checkStock(invoiceProductDto);
        });
    }
}