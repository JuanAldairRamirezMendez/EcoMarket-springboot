package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Role;
import com.ecomarket.infrastructure.persistence.entity.RoleEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T03:16:22-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class RoleEntityMapperImpl implements RoleEntityMapper {

    @Override
    public Role toDomain(RoleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.description( entity.getDescription() );
        role.id( entity.getId() );
        role.name( entity.getName() );

        return role.build();
    }

    @Override
    public RoleEntity toEntity(Role domain) {
        if ( domain == null ) {
            return null;
        }

        RoleEntity.RoleEntityBuilder roleEntity = RoleEntity.builder();

        roleEntity.description( domain.getDescription() );
        roleEntity.id( domain.getId() );
        roleEntity.name( domain.getName() );

        return roleEntity.build();
    }
}
