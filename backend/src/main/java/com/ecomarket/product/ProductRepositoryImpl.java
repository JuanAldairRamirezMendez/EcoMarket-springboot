package com.ecomarket.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final EntityManager em;

    public ProductRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<Product> findByFilters(String q, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Boolean isOrganic, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> preds = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase() + "%";
            preds.add(cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("description")), like)
            ));
        }

        if (categoryId != null) {
            preds.add(cb.equal(root.get("category").get("id"), categoryId));
        }

        if (minPrice != null) {
            preds.add(cb.ge(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            preds.add(cb.le(root.get("price"), maxPrice));
        }

        if (isOrganic != null) {
            preds.add(cb.equal(root.get("isOrganic"), isOrganic));
        }

        cq.where(preds.toArray(new Predicate[0]));
        // order by createdAt desc
        cq.orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<Product> query = em.createQuery(cq);

        // Count query (more efficient than fetching full result list and measuring size)
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Product> countRoot = countCq.from(Product.class);
        List<Predicate> countPreds = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase() + "%";
            countPreds.add(cb.or(
                cb.like(cb.lower(countRoot.get("name")), like),
                cb.like(cb.lower(countRoot.get("description")), like)
            ));
        }

        if (categoryId != null) {
            countPreds.add(cb.equal(countRoot.get("category").get("id"), categoryId));
        }

        if (minPrice != null) {
            countPreds.add(cb.ge(countRoot.get("price"), minPrice));
        }

        if (maxPrice != null) {
            countPreds.add(cb.le(countRoot.get("price"), maxPrice));
        }

        if (isOrganic != null) {
            countPreds.add(cb.equal(countRoot.get("isOrganic"), isOrganic));
        }

        countCq.select(cb.count(countRoot));
        countCq.where(countPreds.toArray(new Predicate[0]));

        Long totalLong = em.createQuery(countCq).getSingleResult();
        int total = totalLong == null ? 0 : totalLong.intValue();

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Product> results = query.getResultList();
        return new PageImpl<>(results, pageable, total);
    }
}
