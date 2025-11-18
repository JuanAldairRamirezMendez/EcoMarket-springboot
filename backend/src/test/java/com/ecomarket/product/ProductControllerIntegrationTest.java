package com.ecomarket.product;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.ecomarket.category.Category;
import com.ecomarket.category.CategoryRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void listProducts_returnsProducts() throws Exception {
        Category cat = new Category();
        cat.setName("TestCat");
        categoryRepository.save(cat);

        Product p = new Product();
        p.setName("Manzana Roja");
        p.setDescription("Manzana fresca");
        p.setPrice(new BigDecimal("1.50"));
        p.setStock(10);
        p.setCategory(cat);
        productRepository.save(p);

        mvc.perform(get("/products").with(user("testuser").roles("USER"))).andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Manzana Roja"));
    }
}
