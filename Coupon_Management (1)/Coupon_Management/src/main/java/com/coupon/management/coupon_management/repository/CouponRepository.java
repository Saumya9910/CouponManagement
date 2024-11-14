package com.coupon.management.coupon_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coupon.management.coupon_management.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>{

}
