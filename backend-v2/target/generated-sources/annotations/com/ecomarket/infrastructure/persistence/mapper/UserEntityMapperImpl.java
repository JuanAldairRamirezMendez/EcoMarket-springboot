package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Role;
import com.ecomarket.domain.model.User;
import com.ecomarket.infrastructure.persistence.entity.RoleEntity;
import com.ecomarket.infrastructure.persistence.entity.UserEntity;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T03:16:23-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserEntityMapperImpl implements UserEntityMapper {

    @Autowired
    private RoleEntityMapper roleEntityMapper;

    @Override
    public User toDomain(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.address( entity.getAddress() );
        user.createdAt( entity.getCreatedAt() );
        user.email( entity.getEmail() );
        user.firstName( entity.getFirstName() );
        user.id( entity.getId() );
        user.isActive( entity.getIsActive() );
        user.lastName( entity.getLastName() );
        user.password( entity.getPassword() );
        user.phone( entity.getPhone() );
        user.roles( roleEntitySetToRoleSet( entity.getRoles() ) );
        user.updatedAt( entity.getUpdatedAt() );
        user.username( entity.getUsername() );

        return user.build();
    }

    @Override
    public UserEntity toEntity(User domain) {
        if ( domain == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.address( domain.getAddress() );
        userEntity.createdAt( domain.getCreatedAt() );
        userEntity.email( domain.getEmail() );
        userEntity.firstName( domain.getFirstName() );
        userEntity.id( domain.getId() );
        userEntity.isActive( domain.getIsActive() );
        userEntity.lastName( domain.getLastName() );
        userEntity.password( domain.getPassword() );
        userEntity.phone( domain.getPhone() );
        userEntity.roles( roleSetToRoleEntitySet( domain.getRoles() ) );
        userEntity.updatedAt( domain.getUpdatedAt() );
        userEntity.username( domain.getUsername() );

        return userEntity.build();
    }

    protected Set<Role> roleEntitySetToRoleSet(Set<RoleEntity> set) {
        if ( set == null ) {
            return null;
        }

        Set<Role> set1 = new LinkedHashSet<Role>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( RoleEntity roleEntity : set ) {
            set1.add( roleEntityMapper.toDomain( roleEntity ) );
        }

        return set1;
    }

    protected Set<RoleEntity> roleSetToRoleEntitySet(Set<Role> set) {
        if ( set == null ) {
            return null;
        }

        Set<RoleEntity> set1 = new LinkedHashSet<RoleEntity>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Role role : set ) {
            set1.add( roleEntityMapper.toEntity( role ) );
        }

        return set1;
    }
}
