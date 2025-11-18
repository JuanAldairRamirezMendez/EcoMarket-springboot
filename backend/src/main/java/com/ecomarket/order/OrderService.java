package com.ecomarket.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecomarket.auth.User;
import com.ecomarket.order.dto.OrderItemRequest;
import com.ecomarket.order.dto.OrderItemResponse;
import com.ecomarket.order.dto.OrderRequest;
import com.ecomarket.order.dto.OrderResponse;
import com.ecomarket.product.Product;
import com.ecomarket.product.ProductRepository;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    public OrderResponse create(User user, OrderRequest request) {
        // Validate products and calculate total
        List<OrderItem> orderItems = request.getItems().stream()
            .map(itemRequest -> {
                Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

                if (product.getStock() < itemRequest.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getName());
                }

                // Reduce stock
                product.setStock(product.getStock() - itemRequest.getQuantity());
                productRepository.save(product);

                return new OrderItem(null, product, itemRequest.getQuantity(), product.getPrice());
            })
            .collect(Collectors.toList());

        BigDecimal totalAmount = orderItems.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(user, orderItems, totalAmount, OrderStatus.PENDING,
                               request.getShippingAddress(), request.getBillingAddress(), request.getNotes());

        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Set order reference in items
        orderItems.forEach(item -> item.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);

        return mapToResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(User user) {
        return orderRepository.findAll().stream()
            .filter(order -> order.getUser().getId().equals(user.getId()))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToResponse(order);
    }

    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    public void cancelOrder(Long id, User user) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be cancelled");
        }

        // Restore stock
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        });

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
            .map(item -> new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
            ))
            .collect(Collectors.toList());

        return new OrderResponse(
            order.getId(),
            order.getUser().getId(),
            order.getUser().getUsername(),
            items,
            order.getTotalAmount(),
            order.getStatus().toString(),
            order.getShippingAddress(),
            order.getBillingAddress(),
            order.getNotes(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }
}