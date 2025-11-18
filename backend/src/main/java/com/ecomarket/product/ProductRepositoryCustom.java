package com.ecomarket.product;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> findByFilters(String q, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Boolean isOrganic, Pageable pageable);
}
