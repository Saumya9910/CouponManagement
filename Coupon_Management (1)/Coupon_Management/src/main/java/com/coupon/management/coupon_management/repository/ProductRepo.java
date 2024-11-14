package com.coupon.management.coupon_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coupon.management.coupon_management.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findProductById(@Param("productId") Long productId);
}


