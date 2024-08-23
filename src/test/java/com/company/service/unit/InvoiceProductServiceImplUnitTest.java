package com.company.service.unit;

import com.company.dto.InvoiceDto;
import com.company.dto.InvoiceProductDto;
import com.company.dto.ProductDto;
import com.company.entity.InvoiceProduct;
import com.company.enums.InvoiceType;
import com.company.exception.InsufficientStockException;
import com.company.exception.InvoiceProductNotFoundException;
import com.company.repository.InvoiceProductRepository;
import com.company.service.InvoiceService;
import com.company.service.impl.InvoiceProductServiceImpl;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceProductServiceImplUnitTest {

    @Mock
    private InvoiceProductRepository invoiceProductRepository;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    @Spy
    private InvoiceProductServiceImpl invoiceProductService;

    private InvoiceProduct invoiceProduct;
    private InvoiceProductDto invoiceProductDto;


    @Test
    void findInvoiceProductById_Test() {

        Long id = 1L;
        InvoiceProduct invoiceProduct = new InvoiceProduct();
        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();

        //Given
        when(invoiceProductRepository.findById(id)).thenReturn(Optional.of(invoiceProduct));
        when(mapperUtil.convert(invoiceProduct,new InvoiceProductDto())).thenReturn(invoiceProductDto);

        //When
        InvoiceProductDto result = invoiceProductService.findInvoiceProductById(id);

        //Then
        assertNotNull(result);
        assertEquals(invoiceProductDto, result);
        verify(invoiceProductRepository).findById(id);
        verify(mapperUtil).convert(invoiceProduct, new InvoiceProductDto());

    }

    @Test
    void findInvoiceProductById_ExceptionTest() {

        when(invoiceProductRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(InvoiceProductNotFoundException.class, () -> {
            invoiceProductService.findInvoiceProductById(1L);
        });

        String expectedMessage = "Invoice Product cannot be found! Id: 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(invoiceProductRepository).findById(1L);
    }

    @Test
    void listAllByInvoiceId_Test() {
        // Arrange
        Long invoiceId = 1L;

        InvoiceProduct invoiceProduct = new InvoiceProduct();
        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();

        invoiceProductDto.setPrice(BigDecimal.valueOf(100));  // Set a non-null value for price
        invoiceProductDto.setQuantity(2);  // Set a non-null value for quantity
        invoiceProductDto.setTax(10);  // Set a non-null value for tax

        // Expected values
        List<InvoiceProduct> invoiceProducts = List.of(invoiceProduct);
        List<InvoiceProductDto> invoiceProductDtos = List.of(invoiceProductDto);

        // Mocking
        when(invoiceProductRepository.retrieveAllByInvoice_IdAndIsDeletedFalse(invoiceId)).thenReturn(invoiceProducts);
        when(mapperUtil.convert(invoiceProduct, new InvoiceProductDto())).thenReturn(invoiceProductDto);
        when(invoiceProductService.calculateInvoiceProductTotal(invoiceProductDto)).thenReturn(BigDecimal.valueOf(220).setScale(2, RoundingMode.HALF_UP));  // Set the correct expected total value

        // Act
        List<InvoiceProductDto> result = invoiceProductService.listAllByInvoiceId(invoiceId);

        // Assert
        assertEquals(invoiceProductDtos, result);
    }


    @Test
    void testSave() {
        Long invoiceId = 1L;

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setQuantity(5);
        invoiceProductDto.setPrice(BigDecimal.TEN);
        invoiceProductDto.setTax(5);

        InvoiceProduct invoiceProduct = new InvoiceProduct();
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setInvoiceType(InvoiceType.SALES);

        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Test Product");
        productDto.setQuantityInStock(10);
        invoiceProductDto.setProduct(productDto);

        when(invoiceService.findById(invoiceId)).thenReturn(invoiceDto);
        when(mapperUtil.convert(any(InvoiceProductDto.class), any(InvoiceProduct.class))).thenReturn(invoiceProduct);
        when(invoiceProductRepository.save(any(InvoiceProduct.class))).thenReturn(invoiceProduct);

        InvoiceProduct result = invoiceProductService.save(invoiceProductDto, invoiceId);

        assertNotNull(result);
        verify(invoiceService).findById(invoiceId);
        verify(invoiceProductRepository).save(invoiceProduct);
    }

    @Test
    void delete_Test() {
        Long invoiceProductId = 1L;
        InvoiceProduct invoiceProduct = new InvoiceProduct();

        when(invoiceProductRepository.findById(invoiceProductId)).thenReturn(Optional.of(invoiceProduct));

        invoiceProductService.delete(invoiceProductId);

        verify(invoiceProductRepository).save(invoiceProduct);
        assertTrue(invoiceProduct.getIsDeleted());
    }

    @Test
    void delete_ExceptionTest() {
        Long invoiceProductId = 1L;

        when(invoiceProductRepository.findById(invoiceProductId)).thenReturn(Optional.empty());

        assertThrows(InvoiceProductNotFoundException.class, () -> {
            invoiceProductService.delete(invoiceProductId);
        });
    }

    @Test
    void testListRemainingApprovedPurchaseInvoiceProducts() {
        Long productId = 1L;
        InvoiceProduct invoiceProduct = new InvoiceProduct();
        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();

        when(invoiceProductRepository.listRemainingApprovedPurchaseInvoiceProducts(productId)).thenReturn(List.of(invoiceProduct));
        when(mapperUtil.convert(invoiceProduct, new InvoiceProductDto())).thenReturn(invoiceProductDto);

        List<InvoiceProductDto> result = invoiceProductService.listRemainingApprovedPurchaseInvoiceProducts(productId);

        assertEquals(1, result.size());
        verify(invoiceProductRepository).listRemainingApprovedPurchaseInvoiceProducts(productId);
        verify(mapperUtil).convert(invoiceProduct, new InvoiceProductDto());
    }



        @Test
        void calculateInvoiceProductTotal_Test() {
            InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
            invoiceProductDto.setPrice(BigDecimal.valueOf(100));
            invoiceProductDto.setQuantity(2);
            invoiceProductDto.setTax(10);

            BigDecimal expectedTotal = BigDecimal.valueOf(220).setScale(2, RoundingMode.HALF_UP);
            BigDecimal result = invoiceProductService.calculateInvoiceProductTotal(invoiceProductDto);

            assertEquals(expectedTotal, result);
        }

    @Test
    void isProductInTheInvoiceProductList_Test() {
        Long productId = 1L;
        Long invoiceId = 1L;

        when(invoiceProductRepository.existsByInvoiceIdAndProductIdAndIsDeletedFalse(invoiceId, productId)).thenReturn(true);

        boolean result = invoiceProductService.isProductInTheInvoiceProductList(productId, invoiceId);

        assertTrue(result);
        verify(invoiceProductRepository).existsByInvoiceIdAndProductIdAndIsDeletedFalse(invoiceId, productId);
    }

    @Test
    void checkStock_Test() {
        ProductDto productDto = new ProductDto();
        productDto.setQuantityInStock(10);
        productDto.setName("Test Product");

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setQuantity(5);
        invoiceProductDto.setProduct(productDto);

        assertDoesNotThrow(() -> invoiceProductService.checkStock(invoiceProductDto));
    }

    @Test
    void checkStock_InsufficientStock_ExceptionTest() {
        ProductDto productDto = new ProductDto();
        productDto.setQuantityInStock(2);
        productDto.setName("Test Product");

        InvoiceProductDto invoiceProductDto = new InvoiceProductDto();
        invoiceProductDto.setQuantity(5);
        invoiceProductDto.setProduct(productDto);

        assertThrows(InsufficientStockException.class, () -> invoiceProductService.checkStock(invoiceProductDto));
    }


    }



