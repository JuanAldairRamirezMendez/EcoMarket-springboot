package com.ecomarket.application.mapper;

import com.ecomarket.application.dto.response.OrderItemResponse;
import com.ecomarket.application.dto.response.OrderResponse;
import com.ecomarket.domain.model.Order;
import com.ecomarket.domain.model.OrderItem;
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
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.billingAddress( order.getBillingAddress() );
        orderResponse.id( order.getId() );
        orderResponse.notes( order.getNotes() );
        orderResponse.orderItems( orderItemListToOrderItemResponseList( order.getOrderItems() ) );
        orderResponse.paymentMethod( order.getPaymentMethod() );
        orderResponse.paymentStatus( order.getPaymentStatus() );
        orderResponse.shippingAddress( order.getShippingAddress() );
        orderResponse.totalAmount( order.getTotalAmount() );
        orderResponse.trackingNumber( order.getTrackingNumber() );

        orderResponse.userId( getUserId(order) );
        orderResponse.username( getUsername(order) );
        orderResponse.status( getStatusString(order) );

        return orderResponse.build();
    }

    @Override
    public List<OrderResponse> toResponseList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderResponse> list = new ArrayList<OrderResponse>( orders.size() );
        for ( Order order : orders ) {
            list.add( toResponse( order ) );
        }

        return list;
    }

    protected List<OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemResponse> list1 = new ArrayList<OrderItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemMapper.toResponse( orderItem ) );
        }

        return list1;
    }
}
