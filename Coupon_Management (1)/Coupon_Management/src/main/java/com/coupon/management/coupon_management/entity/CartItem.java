package com.coupon.management.coupon_management.entity;


public class CartItem {
    private Long productId;
    private Integer quantity;
    private Double price;
    private Double discount;
    
    
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	
	@Override
	public String toString() {
		return "CartItem [productId=" + productId + ", quantity=" + quantity + ", price=" + price + ", discount="
				+ discount + "]";
	}
	
	
	public CartItem(Long productId, Integer quantity, Double price, Double discount) {
		super();
		this.productId = productId;
		this.quantity = quantity;
		this.price = price;
		this.discount = discount;
	}
	public CartItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(double itemDiscount) {
		this.discount = itemDiscount;
	}
    
	
	
    
    
}
