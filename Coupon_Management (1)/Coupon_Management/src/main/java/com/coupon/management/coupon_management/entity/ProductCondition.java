package com.coupon.management.coupon_management.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProductCondition {
    private Long productId;
    private int quantity;
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		return "ProductCondition [productId=" + productId + ", quantity=" + quantity + "]";
	}
	public ProductCondition(Long productId, int quantity) {
		super();
		this.productId = productId;
		this.quantity = quantity;
	}
	public ProductCondition() {
		super();
		
	}
    
    
}
