package com.ecomarket.application.mapper;

import com.ecomarket.application.dto.response.ProductResponse;
import com.ecomarket.domain.model.Category;
import com.ecomarket.domain.model.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-02T03:16:22-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductResponse toResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.categoryId( productCategoryId( product ) );
        productResponse.categoryName( productCategoryName( product ) );
        productResponse.carbonFootprint( product.getCarbonFootprint() );
        productResponse.certifications( product.getCertifications() );
        productResponse.description( product.getDescription() );
        productResponse.id( product.getId() );
        productResponse.imageFilename( product.getImageFilename() );
        productResponse.isActive( product.getIsActive() );
        productResponse.isFeatured( product.getIsFeatured() );
        productResponse.isOrganic( product.getIsOrganic() );
        productResponse.name( product.getName() );
        productResponse.originCountry( product.getOriginCountry() );
        productResponse.price( product.getPrice() );
        productResponse.stockQuantity( product.getStockQuantity() );

        return productResponse.build();
    }

    @Override
    public List<ProductResponse> toResponseList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductResponse> list = new ArrayList<ProductResponse>( products.size() );
        for ( Product product : products ) {
            list.add( toResponse( product ) );
        }

        return list;
    }

    private Long productCategoryId(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String productCategoryName(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
