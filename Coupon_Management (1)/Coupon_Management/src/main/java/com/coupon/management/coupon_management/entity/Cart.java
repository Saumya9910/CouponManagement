package com.coupon.management.coupon_management.entity;


import java.util.List;

public class Cart {
    private List<CartItem> items;
    private double totalPrice;
    
    
	public List<CartItem> getItems() {
		return items;
	}
	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public Cart(List<CartItem> items, double totalPrice) {
		super();
		this.items = items;
		this.totalPrice = totalPrice;
	}
	@Override
	public String toString() {
		return "Cart [items=" + items + ", totalPrice=" + totalPrice + "]";
	}
	public Cart() {
		super();
		// TODO Auto-generated constructor stub
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
}

