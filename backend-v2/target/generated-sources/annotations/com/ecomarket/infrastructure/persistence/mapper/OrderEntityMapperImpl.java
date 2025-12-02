package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Order;
import com.ecomarket.domain.model.OrderItem;
import com.ecomarket.infrastructure.persistence.entity.OrderEntity;
import com.ecomarket.infrastructure.persistence.entity.OrderItemEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T03:16:22-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class OrderEntityMapperImpl implements OrderEntityMapper {

    @Autowired
    private UserEntityMapper userEntityMapper;
    @Autowired
    private OrderItemEntityMapper orderItemEntityMapper;

    @Override
    public Order toDomain(OrderEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.status( stringToStatus( entity.getStatus() ) );
        order.billingAddress( entity.getBillingAddress() );
        order.createdAt( entity.getCreatedAt() );
        order.id( entity.getId() );
        order.notes( entity.getNotes() );
        order.orderItems( orderItemEntityListToOrderItemList( entity.getOrderItems() ) );
        order.paymentMethod( entity.getPaymentMethod() );
        order.paymentStatus( entity.getPaymentStatus() );
        order.shippingAddress( entity.getShippingAddress() );
        order.totalAmount( entity.getTotalAmount() );
        order.trackingNumber( entity.getTrackingNumber() );
        order.updatedAt( entity.getUpdatedAt() );
        order.user( userEntityMapper.toDomain( entity.getUser() ) );

        return order.build();
    }

    @Override
    public OrderEntity toEntity(Order domain) {
        if ( domain == null ) {
            return null;
        }

        OrderEntity.OrderEntityBuilder orderEntity = OrderEntity.builder();

        orderEntity.status( statusToString( domain.getStatus() ) );
        orderEntity.billingAddress( domain.getBillingAddress() );
        orderEntity.createdAt( domain.getCreatedAt() );
        orderEntity.id( domain.getId() );
        orderEntity.notes( domain.getNotes() );
        orderEntity.orderItems( orderItemListToOrderItemEntityList( domain.getOrderItems() ) );
        orderEntity.paymentMethod( domain.getPaymentMethod() );
        orderEntity.paymentStatus( domain.getPaymentStatus() );
        orderEntity.shippingAddress( domain.getShippingAddress() );
        orderEntity.totalAmount( domain.getTotalAmount() );
        orderEntity.trackingNumber( domain.getTrackingNumber() );
        orderEntity.updatedAt( domain.getUpdatedAt() );
        orderEntity.user( userEntityMapper.toEntity( domain.getUser() ) );

        return orderEntity.build();
    }

    protected List<OrderItem> orderItemEntityListToOrderItemList(List<OrderItemEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItem> list1 = new ArrayList<OrderItem>( list.size() );
        for ( OrderItemEntity orderItemEntity : list ) {
            list1.add( orderItemEntityMapper.toDomain( orderItemEntity ) );
        }

        return list1;
    }

    protected List<OrderItemEntity> orderItemListToOrderItemEntityList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemEntity> list1 = new ArrayList<OrderItemEntity>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemEntityMapper.toEntity( orderItem ) );
        }

        return list1;
    }
}
