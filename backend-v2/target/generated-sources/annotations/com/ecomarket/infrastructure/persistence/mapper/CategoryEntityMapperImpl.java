package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Category;
import com.ecomarket.infrastructure.persistence.entity.CategoryEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T03:16:22-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class CategoryEntityMapperImpl implements CategoryEntityMapper {

    @Override
    public Category toDomain(CategoryEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.createdAt( entity.getCreatedAt() );
        category.description( entity.getDescription() );
        category.id( entity.getId() );
        category.imageUrl( entity.getImageUrl() );
        category.isActive( entity.getIsActive() );
        category.name( entity.getName() );
        category.updatedAt( entity.getUpdatedAt() );

        return category.build();
    }

    @Override
    public CategoryEntity toEntity(Category domain) {
        if ( domain == null ) {
            return null;
        }

        CategoryEntity.CategoryEntityBuilder categoryEntity = CategoryEntity.builder();

        categoryEntity.createdAt( domain.getCreatedAt() );
        categoryEntity.description( domain.getDescription() );
        categoryEntity.id( domain.getId() );
        categoryEntity.imageUrl( domain.getImageUrl() );
        categoryEntity.isActive( domain.getIsActive() );
        categoryEntity.name( domain.getName() );
        categoryEntity.updatedAt( domain.getUpdatedAt() );

        return categoryEntity.build();
    }
}
