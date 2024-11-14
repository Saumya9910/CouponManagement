package com.coupon.management.coupon_management;
import com.coupon.management.coupon_management.controller.CouponController;
import com.coupon.management.coupon_management.entity.Cart;
import com.coupon.management.coupon_management.entity.Coupon;
import com.coupon.management.coupon_management.service.CouponService;
import com.coupon.management.coupon_management.repository.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

 public class CouponManagementApplicationTests {

    @Mock
    private CouponService couponService;

    @Mock
    private ProductRepo productRepository;

    @InjectMocks
    private CouponController couponController;

    private Coupon coupon;
    private Cart cart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize a sample coupon for testing
        coupon = new Coupon();
        coupon.setId(1L);
        coupon.setType("cart-wise");

        // Initialize a sample cart for testing
        cart = new Cart();
        cart.setTotalPrice(200.0);
    }

    @Test
    void testCreateCoupon() {
        when(couponService.createCoupon(any(Coupon.class))).thenReturn(coupon);

        ResponseEntity<?> response = couponController.createCoupon(coupon);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(coupon, response.getBody());
        verify(couponService).createCoupon(any(Coupon.class));
    }

    @Test
    void testGetAllCoupons() {
        when(couponService.getAllCoupons()).thenReturn(List.of(coupon));

        List<Coupon> response = couponController.getAllCoupons();

        assertEquals(1, response.size());
        assertEquals(coupon, response.get(0));
        verify(couponService).getAllCoupons();
    }

    @Test
    void testGetCouponById() {
        when(couponService.getCouponById(1L)).thenReturn(Optional.of(coupon));

        ResponseEntity<Coupon> response = couponController.getCouponById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(coupon, response.getBody());
        verify(couponService).getCouponById(1L);
    }

    @Test
    void testGetCouponById_NotFound() {
        when(couponService.getCouponById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Coupon> response = couponController.getCouponById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateCoupon() {
        when(couponService.updateCoupon(any(Long.class), any(Coupon.class))).thenReturn(coupon);

        ResponseEntity<Coupon> response = couponController.updateCoupon(1L, coupon);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(coupon, response.getBody());
        verify(couponService).updateCoupon(1L, coupon);
    }

    @Test
    void testDeleteCoupon() {
        ResponseEntity<Void> response = couponController.deleteCoupon(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(couponService).deleteCoupon(1L);
    }

    @Test
    void testGetApplicableCoupons() {
        when(couponService.getAllCoupons()).thenReturn(List.of(coupon));

        ResponseEntity<?> response = couponController.getApplicableCoupons(cart);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("applicable_coupons", responseBody.keySet().iterator().next());
    }

    @Test
    void testApplyCouponToCart() {
        when(couponService.getCouponById(1L)).thenReturn(Optional.of(coupon));

        ResponseEntity<?> response = couponController.applyCouponToCart(1L, cart);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(cart, responseBody.get("updated_cart"));
        verify(couponService).getCouponById(1L);
    }

    @Test
    void testApplyCouponToCart_NotFound() {
        when(couponService.getCouponById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = couponController.applyCouponToCart(1L, cart);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
