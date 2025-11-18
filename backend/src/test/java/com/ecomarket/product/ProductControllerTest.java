package com.ecomarket.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.ecomarket.product.dto.PagedProductResponse;
import com.ecomarket.product.dto.ProductRequest;
import com.ecomarket.product.dto.ProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration"
})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void list_ShouldReturnPagedProducts() throws Exception {
        // Given
        ProductResponse product = createSampleProductResponse();
        Page<ProductResponse> productPage = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productService.list(any(Pageable.class))).thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Test Product"))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(true));

        verify(productService).list(any(Pageable.class));
    }

    @Test
    void get_ShouldReturnProduct() throws Exception {
        // Given
        Long productId = 1L;
        ProductResponse product = createSampleProductResponse();
        when(productService.getById(productId)).thenReturn(product);

        // When & Then
        mockMvc.perform(get("/products/{id}", productId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService).getById(productId);
    }

    @Test
    void get_ShouldReturn404_WhenProductNotFound() throws Exception {
        // Given
        Long productId = 1L;
        when(productService.getById(productId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // When & Then
        mockMvc.perform(get("/products/{id}", productId))
            .andExpect(status().isNotFound());

        verify(productService).getById(productId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturnCreatedProduct() throws Exception {
        // Given
        ProductRequest request = createSampleProductRequest();
        ProductResponse created = createSampleProductResponse();
        when(productService.create(any(ProductRequest.class))).thenReturn(created);

        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/products/1"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService).create(any(ProductRequest.class));
    }

    @Test
    void create_ShouldReturn403_WhenNotAdmin() throws Exception {
        // Given
        ProductRequest request = createSampleProductRequest();
        ProductResponse created = createSampleProductResponse();
        when(productService.create(any(ProductRequest.class))).thenReturn(created);

        // When & Then
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        Long productId = 1L;
        ProductRequest request = createSampleProductRequest();
        ProductResponse updated = createSampleProductResponse();
        when(productService.update(eq(productId), any(ProductRequest.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService).update(eq(productId), any(ProductRequest.class));
    }

    @Test
    void update_ShouldReturn403_WhenNotAdmin() throws Exception {
        // Given
        Long productId = 1L;
        ProductRequest request = createSampleProductRequest();
        ProductResponse updated = createSampleProductResponse();
        when(productService.update(eq(productId), any(ProductRequest.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturnNoContent() throws Exception {
        // Given
        Long productId = 1L;

        // When & Then
        mockMvc.perform(delete("/products/{id}", productId))
            .andExpect(status().isNoContent());

        verify(productService).delete(productId);
    }

    @Test
    void delete_ShouldReturn403_WhenNotAdmin() throws Exception {
        // Given
        Long productId = 1L;

        // When & Then
        mockMvc.perform(delete("/products/{id}", productId))
            .andExpect(status().isForbidden());
    }

    private ProductResponse createSampleProductResponse() {
        return new ProductResponse(1L, "Test Product", "Test Description", BigDecimal.valueOf(10.0), 100, 1L, "Test Category", "test.jpg", "/ecomarket/api/images/test.jpg", true, "Organic", "Test Country", LocalDateTime.now(), LocalDateTime.now());
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