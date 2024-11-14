package com.coupon.management.coupon_management.entity;

import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

@Entity
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String type; // e.g., "cart-wise", "product-wise", "bxgy"
    
    @Embedded
    private CouponDetails  details;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expirationDate;
    
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CouponDetails getDetails() {
		return details;
	}

	public void setDetails(CouponDetails details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "Coupon [id=" + id + ", type=" + type + ", details=" + details + "]";
	}

	public Coupon(Long id, String type, CouponDetails details) {
		super();
		this.id = id;
		this.type = type;
		this.details = details;
	}

	public Coupon() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime localDateTime) {
		this.expirationDate = localDateTime;
	}
    
	
}
    
    
    

    
    



