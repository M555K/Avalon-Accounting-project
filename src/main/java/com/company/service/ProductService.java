package com.company.service;

import com.company.dto.InvoiceProductDto;
import com.company.dto.ProductDto;
import com.company.entity.Product;

import java.util.List;

public interface ProductService {

    List<ProductDto> listAllProducts();

    ProductDto findById(Long id);

    List<ProductDto> listAvailableProducts();

    void delete(Long id);

    void save(ProductDto product);

    ProductDto update(ProductDto product);

}
