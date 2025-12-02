package com.ecomarket.infrastructure.persistence.mapper;

import com.ecomarket.domain.model.Product;
import com.ecomarket.infrastructure.persistence.entity.ProductEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T03:16:23-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ProductEntityMapperImpl implements ProductEntityMapper {

    @Autowired
    private CategoryEntityMapper categoryEntityMapper;

    @Override
    public Product toDomain(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.carbonFootprint( entity.getCarbonFootprint() );
        product.category( categoryEntityMapper.toDomain( entity.getCategory() ) );
        product.certifications( entity.getCertifications() );
        product.createdAt( entity.getCreatedAt() );
        product.description( entity.getDescription() );
        product.id( entity.getId() );
        product.imageFilename( entity.getImageFilename() );
        product.isActive( entity.getIsActive() );
        product.isFeatured( entity.getIsFeatured() );
        product.isOrganic( entity.getIsOrganic() );
        product.name( entity.getName() );
        product.originCountry( entity.getOriginCountry() );
        product.price( entity.getPrice() );
        product.stockQuantity( entity.getStockQuantity() );
        product.updatedAt( entity.getUpdatedAt() );

        return product.build();
    }

    @Override
    public ProductEntity toEntity(Product domain) {
        if ( domain == null ) {
            return null;
        }

        ProductEntity.ProductEntityBuilder productEntity = ProductEntity.builder();

        productEntity.carbonFootprint( domain.getCarbonFootprint() );
        productEntity.category( categoryEntityMapper.toEntity( domain.getCategory() ) );
        productEntity.certifications( domain.getCertifications() );
        productEntity.createdAt( domain.getCreatedAt() );
        productEntity.description( domain.getDescription() );
        productEntity.id( domain.getId() );
        productEntity.imageFilename( domain.getImageFilename() );
        productEntity.isActive( domain.getIsActive() );
        productEntity.isFeatured( domain.getIsFeatured() );
        productEntity.isOrganic( domain.getIsOrganic() );
        productEntity.name( domain.getName() );
        productEntity.originCountry( domain.getOriginCountry() );
        productEntity.price( domain.getPrice() );
        productEntity.stockQuantity( domain.getStockQuantity() );
        productEntity.updatedAt( domain.getUpdatedAt() );

        return productEntity.build();
    }
}
