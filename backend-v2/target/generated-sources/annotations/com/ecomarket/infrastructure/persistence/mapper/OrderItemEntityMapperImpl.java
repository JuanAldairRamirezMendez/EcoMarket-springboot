package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Order;
import com.ecomarket.domain.model.OrderItem;
import com.ecomarket.domain.model.Role;
import com.ecomarket.domain.model.User;
import com.ecomarket.infrastructure.persistence.entity.OrderEntity;
import com.ecomarket.infrastructure.persistence.entity.OrderItemEntity;
import com.ecomarket.infrastructure.persistence.entity.RoleEntity;
import com.ecomarket.infrastructure.persistence.entity.UserEntity;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T03:16:22-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class OrderItemEntityMapperImpl implements OrderItemEntityMapper {

    @Autowired
    private ProductEntityMapper productEntityMapper;

    @Override
    public OrderItem toDomain(OrderItemEntity entity) {
        if ( entity == null ) {
            return null;
        }

        OrderItem.OrderItemBuilder orderItem = OrderItem.builder();

        orderItem.id( entity.getId() );
        orderItem.order( orderEntityToOrder( entity.getOrder() ) );
        orderItem.product( productEntityMapper.toDomain( entity.getProduct() ) );
        orderItem.quantity( entity.getQuantity() );
        orderItem.totalPrice( entity.getTotalPrice() );
        orderItem.unitPrice( entity.getUnitPrice() );

        return orderItem.build();
    }

    @Override
    public OrderItemEntity toEntity(OrderItem domain) {
        if ( domain == null ) {
            return null;
        }

        OrderItemEntity.OrderItemEntityBuilder orderItemEntity = OrderItemEntity.builder();

        orderItemEntity.id( domain.getId() );
        orderItemEntity.order( orderToOrderEntity( domain.getOrder() ) );
        orderItemEntity.product( productEntityMapper.toEntity( domain.getProduct() ) );
        orderItemEntity.quantity( domain.getQuantity() );
        orderItemEntity.totalPrice( domain.getTotalPrice() );
        orderItemEntity.unitPrice( domain.getUnitPrice() );

        return orderItemEntity.build();
    }

    protected List<OrderItem> orderItemEntityListToOrderItemList(List<OrderItemEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItem> list1 = new ArrayList<OrderItem>( list.size() );
        for ( OrderItemEntity orderItemEntity : list ) {
            list1.add( toDomain( orderItemEntity ) );
        }

        return list1;
    }

    protected Role roleEntityToRole(RoleEntity roleEntity) {
        if ( roleEntity == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.description( roleEntity.getDescription() );
        role.id( roleEntity.getId() );
        role.name( roleEntity.getName() );

        return role.build();
    }

    protected Set<Role> roleEntitySetToRoleSet(Set<RoleEntity> set) {
        if ( set == null ) {
            return null;
        }

        Set<Role> set1 = new LinkedHashSet<Role>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( RoleEntity roleEntity : set ) {
            set1.add( roleEntityToRole( roleEntity ) );
        }

        return set1;
    }

    protected User userEntityToUser(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.address( userEntity.getAddress() );
        user.createdAt( userEntity.getCreatedAt() );
        user.email( userEntity.getEmail() );
        user.firstName( userEntity.getFirstName() );
        user.id( userEntity.getId() );
        user.isActive( userEntity.getIsActive() );
        user.lastName( userEntity.getLastName() );
        user.password( userEntity.getPassword() );
        user.phone( userEntity.getPhone() );
        user.roles( roleEntitySetToRoleSet( userEntity.getRoles() ) );
        user.updatedAt( userEntity.getUpdatedAt() );
        user.username( userEntity.getUsername() );

        return user.build();
    }

    protected Order orderEntityToOrder(OrderEntity orderEntity) {
        if ( orderEntity == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.billingAddress( orderEntity.getBillingAddress() );
        order.createdAt( orderEntity.getCreatedAt() );
        order.id( orderEntity.getId() );
        order.notes( orderEntity.getNotes() );
        order.orderItems( orderItemEntityListToOrderItemList( orderEntity.getOrderItems() ) );
        order.paymentMethod( orderEntity.getPaymentMethod() );
        order.paymentStatus( orderEntity.getPaymentStatus() );
        order.shippingAddress( orderEntity.getShippingAddress() );
        if ( orderEntity.getStatus() != null ) {
            order.status( Enum.valueOf( Order.OrderStatus.class, orderEntity.getStatus() ) );
        }
        order.totalAmount( orderEntity.getTotalAmount() );
        order.trackingNumber( orderEntity.getTrackingNumber() );
        order.updatedAt( orderEntity.getUpdatedAt() );
        order.user( userEntityToUser( orderEntity.getUser() ) );

        return order.build();
    }

    protected List<OrderItemEntity> orderItemListToOrderItemEntityList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemEntity> list1 = new ArrayList<OrderItemEntity>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toEntity( orderItem ) );
        }

        return list1;
    }

    protected RoleEntity roleToRoleEntity(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleEntity.RoleEntityBuilder roleEntity = RoleEntity.builder();

        roleEntity.description( role.getDescription() );
        roleEntity.id( role.getId() );
        roleEntity.name( role.getName() );

        return roleEntity.build();
    }

    protected Set<RoleEntity> roleSetToRoleEntitySet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleEntity> set1 = new LinkedHashSet<RoleEntity>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleToRoleEntity( role ) );
        }

        return set1;
    }

    protected UserEntity userToUserEntity(User user) {
        if ( user == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.address( user.getAddress() );
        userEntity.createdAt( user.getCreatedAt() );
        userEntity.email( user.getEmail() );
        userEntity.firstName( user.getFirstName() );
        userEntity.id( user.getId() );
        userEntity.isActive( user.getIsActive() );
        userEntity.lastName( user.getLastName() );
        userEntity.password( user.getPassword() );
        userEntity.phone( user.getPhone() );
        userEntity.roles( roleSetToRoleEntitySet( user.getRoles() ) );
        userEntity.updatedAt( user.getUpdatedAt() );
        userEntity.username( user.getUsername() );

        return userEntity.build();
    }

    protected OrderEntity orderToOrderEntity(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderEntity.OrderEntityBuilder orderEntity = OrderEntity.builder();

        orderEntity.billingAddress( order.getBillingAddress() );
        orderEntity.createdAt( order.getCreatedAt() );
        orderEntity.id( order.getId() );
        orderEntity.notes( order.getNotes() );
        orderEntity.orderItems( orderItemListToOrderItemEntityList( order.getOrderItems() ) );
        orderEntity.paymentMethod( order.getPaymentMethod() );
        orderEntity.paymentStatus( order.getPaymentStatus() );
        orderEntity.shippingAddress( order.getShippingAddress() );
        if ( order.getStatus() != null ) {
            orderEntity.status( order.getStatus().name() );
        }
        orderEntity.totalAmount( order.getTotalAmount() );
        orderEntity.trackingNumber( order.getTrackingNumber() );
        orderEntity.updatedAt( order.getUpdatedAt() );
        orderEntity.user( userToUserEntity( order.getUser() ) );

        return orderEntity.build();
    }
}
