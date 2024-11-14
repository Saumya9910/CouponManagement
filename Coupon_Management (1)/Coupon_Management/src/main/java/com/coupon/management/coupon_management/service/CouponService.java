package com.coupon.management.coupon_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coupon.management.coupon_management.entity.Coupon;
import com.coupon.management.coupon_management.entity.ProductCondition;
import com.coupon.management.coupon_management.exception.CouponNotFoundException;
import com.coupon.management.coupon_management.exception.InvalidProductException;
import com.coupon.management.coupon_management.repository.CouponRepository;
import com.coupon.management.coupon_management.repository.ProductRepo;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProductRepo productRepository;

    
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        List<ProductCondition> updatedBuyProducts = new ArrayList<>();
        List<ProductCondition> updatedGetProducts = new ArrayList<>();

        for (ProductCondition buyProductCondition : coupon.getDetails().getBuyProducts()) {
            if (productRepository.existsById(buyProductCondition.getProductId())) {
                updatedBuyProducts.add(buyProductCondition);
            } else {
                throw new InvalidProductException("Product with ID " + buyProductCondition.getProductId() + " does not exist.");
            }
        }

        
        for (ProductCondition getProductCondition : coupon.getDetails().getGetProducts()) {
            if (productRepository.existsById(getProductCondition.getProductId())) {
                updatedGetProducts.add(getProductCondition);
            } else {
                throw new InvalidProductException("Product with ID " + getProductCondition.getProductId() + " does not exist.");
            }
        } 
        coupon.getDetails().setBuyProducts(updatedBuyProducts);
        coupon.getDetails().setGetProducts(updatedGetProducts);     
        if (coupon.getExpirationDate() == null) {
            coupon.setExpirationDate(LocalDateTime.now().plusDays(30));
        }      
        return couponRepository.save(coupon);
    }



    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }


    public Coupon updateCoupon(Long id, Coupon updatedCoupon) {
        return couponRepository.findById(id)
                .map(coupon -> {
                    coupon.setType(updatedCoupon.getType());
                    coupon.setDetails(updatedCoupon.getDetails());
                    return couponRepository.save(coupon);
                })
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));
    }

    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new CouponNotFoundException("Coupon with ID " + id + " not found");
        }
        couponRepository.deleteById(id);
    }
}
