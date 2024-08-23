package com.company.service.unit;

import com.company.dto.CompanyDto;
import com.company.dto.ProductDto;
import com.company.entity.Company;
import com.company.entity.Product;
import com.company.exception.ProductNotFoundException;
import com.company.repository.ProductRepository;
import com.company.service.CompanyService;
import com.company.service.SecurityService;
import com.company.service.impl.ProductServiceImpl;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MapperUtil mapperUtil;

    @Mock
    private CompanyService companyService;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ProductServiceImpl productService;

    private CompanyDto companyDto;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        companyDto = new CompanyDto();
        companyDto.setId(1L);

        product = new Product();
        product.setId(1L);

        productDto = new ProductDto();
        productDto.setId(1L);
    }

    // ----------------------------------------------------------------------------------------------
    // Tests for listAllProducts

    @Test
    void test_ListAllProducts_ShouldReturnProductDtoList() {
        mockCompanyService(companyDto.getId());

        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findByCompanyIdAndIsDeletedFalseOrderByCategoryAscNameAsc(companyDto.getId()))
                .thenReturn(products);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class)))
                .thenReturn(new ProductDto(), new ProductDto());

        List<ProductDto> result = productService.listAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto instanceof ProductDto));
    }

    @Test
    void test_ListAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() {
        mockCompanyService(companyDto.getId());

        when(productRepository.findByCompanyIdAndIsDeletedFalseOrderByCategoryAscNameAsc(companyDto.getId()))
                .thenReturn(new ArrayList<>());

        List<ProductDto> result = productService.listAllProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ----------------------------------------------------------------------------------------------
    // Tests for findById

    @Test
    void test_FindById_ShouldReturnProductDto_WhenProductExists() {
        when(productRepository.findByIdAndIsDeleted(product.getId(), false)).thenReturn(product);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);

        ProductDto result = productService.findById(product.getId());

        assertNotNull(result);
        assertEquals(productDto, result);
    }

    @Test
    void test_FindById_ShouldReturnNull_WhenProductNotFound() {
        when(productRepository.findByIdAndIsDeleted(product.getId(), false)).thenReturn(null);



        ProductNotFoundException thrownException = assertThrows(ProductNotFoundException.class, () -> {
            ProductDto result = productService.findById(product.getId());
        });

        assertEquals("Product not found...", thrownException.getMessage());
    }

    // ----------------------------------------------------------------------------------------------
    // Tests for listAvailableProducts

    @Test
    void test_ListAvailableProducts_ShouldReturnProductDtoList() {
        mockCompanyService(companyDto.getId());

        List<Product> products = List.of(product, new Product());
        when(productRepository.retrieveAllAvailableProductsForCompany(companyDto.getId())).thenReturn(products);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class)))
                .thenReturn(new ProductDto(), new ProductDto());

        List<ProductDto> result = productService.listAvailableProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void test_ListAvailableProducts_ShouldReturnEmptyList_WhenNoProductsAvailable() {
        mockCompanyService(companyDto.getId());

        when(productRepository.retrieveAllAvailableProductsForCompany(companyDto.getId())).thenReturn(new ArrayList<>());

        List<ProductDto> result = productService.listAvailableProducts();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ----------------------------------------------------------------------------------------------
    // Tests for delete

    @Test
    void delete_ShouldNotDeleteProduct_WhenProductHasInvoiceProduct() {
        // Arrange
        productDto.setHasInvoiceProduct(true);
        when(productRepository.findByIdAndIsDeleted(anyLong(), anyBoolean())).thenReturn(product);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);

        // Act
        productService.delete(1L);

        // Assert
        assertFalse(product.getIsDeleted(), "Product should not be deleted when it has invoice products.");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void delete_ShouldNotDeleteProduct_WhenProductQuantityInStockIsGreaterThanOne() {
        // Arrange
        productDto.setQuantityInStock(10);
        when(productRepository.findByIdAndIsDeleted(anyLong(), anyBoolean())).thenReturn(product);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);

        // Act
        productService.delete(1L);

        // Assert
        assertFalse(product.getIsDeleted(), "Product should not be deleted when quantity in stock is greater than one.");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void delete_ShouldDeleteProduct_WhenProductCanBeDeleted() {
        // Arrange
        productDto.setHasInvoiceProduct(false);
        productDto.setQuantityInStock(0);
        when(productRepository.findByIdAndIsDeleted(anyLong(), anyBoolean())).thenReturn(product);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        productService.delete(1L);

        // Assert
        assertTrue(product.getIsDeleted(), "Product should be marked as deleted.");
        verify(productRepository, times(1)).save(product);
    }

    // ----------------------------------------------------------------------------------------------
    // Tests for save

    @Test
    void test_SaveProduct_ShouldReturnSavedProduct() {
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(mapperUtil.convert(any(ProductDto.class), any(Product.class))).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);

        productService.save(productDto);

        verify(productRepository).save(product);
    }

    // ----------------------------------------------------------------------------------------------
    // Tests for save

    @Test
    void test_UpdateProduct_ShouldUpdateProduct_WhenProductExists() {
        // Arrange
        Company company = new Company();
        product.setCompany(company);
        when(productRepository.findByIdAndIsDeleted(productDto.getId(), false)).thenReturn(product);
        when(mapperUtil.convert(any(ProductDto.class), any(Product.class))).thenReturn(product);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(mapperUtil.convert(any(CompanyDto.class), any(Company.class))).thenReturn(company);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductDto updatedProductDto = productService.update(productDto);

        // Assert
        assertNotNull(updatedProductDto);
        assertEquals(productDto.getName(), updatedProductDto.getName());
        assertEquals(productDto.getCategory(), updatedProductDto.getCategory());
        assertEquals(productDto.getProductUnit(), updatedProductDto.getProductUnit());
        assertEquals(productDto.getLowLimitAlert(), updatedProductDto.getLowLimitAlert());
        verify(productRepository).save(product);
    }

    @Test
    void test_UpdateProduct_ShouldThrowException_WhenProductDoesNotExist() {
        // Arrange
        when(productRepository.findByIdAndIsDeleted(anyLong(), anyBoolean())).thenReturn(null);

        // Act & Assert
        ProductNotFoundException thrownException = assertThrows(ProductNotFoundException.class, () -> {
            productService.update(productDto);
        });

        assertEquals("Product cannot be found or has already been deleted! Id: " + 1L, thrownException.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    // ----------------------------------------------------------------------------------------------
    // Helper Methods

    private void mockCompanyService(Long companyId) {
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
    }
}
