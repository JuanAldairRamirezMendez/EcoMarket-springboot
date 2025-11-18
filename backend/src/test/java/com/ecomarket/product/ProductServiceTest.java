package com.ecomarket.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import com.ecomarket.category.Category;
import com.ecomarket.category.CategoryRepository;
import com.ecomarket.product.dto.ProductRequest;
import com.ecomarket.product.dto.ProductResponse;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void list_ShouldReturnPagedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Product product = createSampleProduct();
        Page<Product> productPage = new PageImpl<>(java.util.List.of(product));
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<ProductResponse> result = productService.list(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Product");
        verify(productRepository).findAll(pageable);
    }

    @Test
    void getById_ShouldReturnProduct_WhenExists() {
        // Given
        Long productId = 1L;
        Product product = createSampleProduct();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        ProductResponse result = productService.getById(productId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).findById(productId);
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        // Given
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.getById(productId))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Product not found");
        verify(productRepository).findById(productId);
    }

    @Test
    void create_ShouldReturnCreatedProduct() {
        // Given
        ProductRequest request = createSampleProductRequest();
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Product savedProduct = createSampleProduct();
        savedProduct.setId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        ProductResponse result = productService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getCategoryId()).isEqualTo(1L);
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void create_ShouldThrowException_WhenCategoryNotFound() {
        // Given
        ProductRequest request = createSampleProductRequest();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.create(request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Category not found");
        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_ShouldReturnUpdatedProduct() {
        // Given
        Long productId = 1L;
        ProductRequest request = createSampleProductRequest();
        Product existingProduct = createSampleProduct();
        existingProduct.setId(productId);

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Product updatedProduct = createSampleProduct();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Product");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        ProductResponse result = productService.update(productId, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(productId);
        verify(productRepository).findById(productId);
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_ShouldThrowException_WhenProductNotFound() {
        // Given
        Long productId = 1L;
        ProductRequest request = createSampleProductRequest();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.update(productId, request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Product not found");
        verify(productRepository).findById(productId);
        verify(categoryRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void delete_ShouldDeleteProduct_WhenExists() {
        // Given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);

        // When
        productService.delete(productId);

        // Then
        verify(productRepository).existsById(productId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> productService.delete(productId))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Product not found");
        verify(productRepository).existsById(productId);
        verify(productRepository, never()).deleteById(productId);
    }

    private Product createSampleProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setStock(100);
        product.setImageFilename("test.jpg");
        product.setIsOrganic(true);
        product.setCertifications("Organic");
        product.setOriginCountry("Test Country");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        product.setCategory(category);

        return product;
    }

    private ProductRequest createSampleProductRequest() {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(BigDecimal.valueOf(10.0));
        request.setStock(100);
        request.setCategoryId(1L);
        request.setImageFilename("test.jpg");
        request.setIsOrganic(true);
        request.setCertifications("Organic");
        request.setOriginCountry("Test Country");
        return request;
    }
}