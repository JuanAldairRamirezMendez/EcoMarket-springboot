package com.ecomarket.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.ecomarket.auth.Role;
import com.ecomarket.auth.User;
import com.ecomarket.order.dto.OrderItemRequest;
import com.ecomarket.order.dto.OrderItemResponse;
import com.ecomarket.order.dto.OrderRequest;
import com.ecomarket.order.dto.OrderResponse;
import com.ecomarket.security.TestSecurityConfig;
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
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        // Create test users
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user");
        testUser.setEmail("user@test.com");
        Set<Role> userRoles = new HashSet<>();
        Role userRole = new Role(1L, "ROLE_USER");
        userRoles.add(userRole);
        testUser.setRoles(userRoles);

        testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setUsername("admin");
        testAdmin.setEmail("admin@test.com");
        Set<Role> adminRoles = new HashSet<>();
        Role adminRole = new Role(2L, "ROLE_ADMIN");
        adminRoles.add(adminRole);
        testAdmin.setRoles(adminRoles);

        // Set default authentication context with User entity as principal
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testUser, null, testUser.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                .toList())
        );
    }

    @Test
    void create_ShouldReturnCreatedOrder() throws Exception {
        // Given
        OrderRequest request = createSampleOrderRequest();
        OrderResponse created = createSampleOrderResponse();
        when(orderService.create(any(), any(OrderRequest.class))).thenReturn(created);

        // When & Then
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/orders/1"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.totalAmount").value(1299.99))
            .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).create(any(), any(OrderRequest.class));
    }

    @Test
    void create_ShouldReturn403_WhenNotAuthenticated() throws Exception {
        // Given
        OrderRequest request = createSampleOrderRequest();

        // When & Then
        mockMvc.perform(post("/orders")
                .with(SecurityMockMvcRequestPostProcessors.anonymous())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden());
    }

    @Test
    void getUserOrders_ShouldReturnUserOrders() throws Exception {
        // Given
        List<OrderResponse> orders = List.of(createSampleOrderResponse());
        when(orderService.getUserOrders(any())).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/orders"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(1));

        verify(orderService).getUserOrders(any());
    }

    @Test
    void getUserOrders_ShouldReturn403_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/orders")
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
            .andExpect(status().isForbidden());
    }

    @Test
    void get_ShouldReturnOrder_WhenUserOwnsOrder() throws Exception {
        // Given
        Long orderId = 1L;
        OrderResponse order = createSampleOrderResponse();
        when(orderService.getById(orderId)).thenReturn(order);

        // When & Then
        mockMvc.perform(get("/orders/{id}", orderId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.totalAmount").value(1299.99));

        verify(orderService).getById(orderId);
    }

    @Test
    void get_ShouldReturn403_WhenNotAuthenticated() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(get("/orders/{id}", orderId)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
            .andExpect(status().isForbidden());
    }

    @Test
    void updateStatus_ShouldReturnUpdatedOrder() throws Exception {
        // Set admin authentication context
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(testAdmin, null, testAdmin.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.getName()))
                .toList())
        );

        // Given
        Long orderId = 1L;
        OrderResponse updated = createSampleOrderResponse();
        updated.setStatus("CONFIRMED");
        when(orderService.updateStatus(orderId, OrderStatus.CONFIRMED)).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/orders/{id}/status", orderId)
                .param("status", "CONFIRMED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderService).updateStatus(orderId, OrderStatus.CONFIRMED);
    }

    @Test
    void updateStatus_ShouldReturn403_WhenNotAdmin() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(put("/orders/{id}/status", orderId)
                .param("status", "CONFIRMED"))
            .andExpect(status().isForbidden());
    }

    @Test
    void cancel_ShouldReturnNoContent() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(delete("/orders/{id}", orderId))
            .andExpect(status().isNoContent());

        verify(orderService).cancelOrder(eq(orderId), any());
    }

    @Test
    void cancel_ShouldReturn403_WhenNotAuthenticated() throws Exception {
        // Given
        Long orderId = 1L;

        // When & Then
        mockMvc.perform(delete("/orders/{id}", orderId)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
            .andExpect(status().isForbidden());
    }

    private OrderResponse createSampleOrderResponse() {
        OrderItemResponse item = new OrderItemResponse(1L, 1L, "Gaming Laptop", 1, BigDecimal.valueOf(1299.99), BigDecimal.valueOf(1299.99));
        return new OrderResponse(1L, 1L, "user", List.of(item), BigDecimal.valueOf(1299.99), "PENDING",
                               "123 Main St", "123 Main St", "Test order", LocalDateTime.now(), LocalDateTime.now());
    }

    private OrderRequest createSampleOrderRequest() {
        OrderItemRequest item = new OrderItemRequest(1L, 1);
        OrderRequest request = new OrderRequest();
        request.setItems(List.of(item));
        request.setShippingAddress("123 Main St");
        request.setBillingAddress("123 Main St");
        request.setNotes("Test order");
        return request;
    }
}