package com.company.service.integration;

import com.company.dto.CompanyDto;
import com.company.dto.ProductDto;
import com.company.entity.Product;
import com.company.exception.ProductNotFoundException;
import com.company.repository.ProductRepository;
import com.company.service.CompanyService;
import com.company.service.ProductService;
import com.company.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceImplIntegrationTest {

    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private MapperUtil mapperUtil;

    private Product product;
    private ProductDto productDto;
    private CompanyDto companyDto;

    @BeforeEach
    void setUp() {
        companyDto = new CompanyDto();
        companyDto.setId(1L);

        product = new Product();
        product.setId(1L);

        productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setQuantityInStock(0);
    }

    @Test
    void testUpdateProduct_ShouldReturnUpdatedProductDto() {
        when(productRepository.findByIdAndIsDeleted(anyLong(), eq(false))).thenReturn(product);
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(mapperUtil.convert(any(ProductDto.class), any(Product.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);

        ProductDto updatedProductDto = productService.update(productDto);

        assertNotNull(updatedProductDto);
        assertEquals(productDto.getId(), updatedProductDto.getId());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testFindById_ShouldReturnProductDto_WhenProductExists() {
        when(productRepository.findByIdAndIsDeleted(1L, false)).thenReturn(product);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);

        ProductDto foundProduct = productService.findById(1L);

        assertNotNull(foundProduct);
        assertEquals(productDto.getId(), foundProduct.getId());
    }

    @Test
    void testFindById_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findByIdAndIsDeleted(1L, false)).thenReturn(null);

        assertThrows(ProductNotFoundException.class, () -> productService.findById(1L));
    }

    @Test
    void testListAllProducts_ShouldReturnListOfProductDtos() {
        when(companyService.getCompanyByLoggedInUser()).thenReturn(companyDto);
        when(productRepository.findByCompanyIdAndIsDeletedFalseOrderByCategoryAscNameAsc(anyLong()))
                .thenReturn(Collections.singletonList(product));
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);

        var productDtos = productService.listAllProducts();

        assertNotNull(productDtos);
        assertEquals(1, productDtos.size());
        assertEquals(productDto.getId(), productDtos.get(0).getId());
    }


    @Test
    void testDeleteProduct_ShouldMarkProductAsDeleted() {
        when(productRepository.findByIdAndIsDeleted(1L, false)).thenReturn(product);
        when(mapperUtil.convert(any(Product.class), any(ProductDto.class))).thenReturn(productDto);

        productService.delete(1L);

        assertTrue(product.getIsDeleted());
        verify(productRepository).save(any(Product.class));
    }

}

