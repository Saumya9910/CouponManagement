package com.coupon.management.coupon_management.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;


@Embeddable
public class CouponDetails {
	
	    private double threshold; // for cart-wise
	    private double discount;
	    
	    @ElementCollection
	    @CollectionTable(name = "buy_product_conditions", joinColumns = @JoinColumn(name = "coupon_id"))
	    @AttributeOverrides({
	        @AttributeOverride(name = "productId", column = @Column(name = "buy_product_id")),
	        @AttributeOverride(name = "quantity", column = @Column(name = "buy_quantity"))
	    })
	    private List<ProductCondition> buyProducts = new ArrayList<>(); // for BxGy

	    @ElementCollection
	    @CollectionTable(name = "get_product_conditions", joinColumns = @JoinColumn(name = "coupon_id"))
	    @AttributeOverrides({
	        @AttributeOverride(name = "productId", column = @Column(name = "get_product_id")),
	        @AttributeOverride(name = "quantity", column = @Column(name = "get_quantity"))
	    })
	    private List<ProductCondition> getProducts = new ArrayList<>(); // for BxGy
	    
	    private int repetitionLimit;   
		public double getThreshold() {
			return threshold;
		}
		public void setThreshold(double threshold) {
			this.threshold = threshold;
		}
		public double getDiscount() {
			return discount;
		}
		public void setDiscount(double discount) {
			this.discount = discount;
		}
		public List<ProductCondition> getBuyProducts() {
			return buyProducts;
		}
		public void setBuyProducts(List<ProductCondition> buyProducts) {
			this.buyProducts = buyProducts;
		}
		public List<ProductCondition> getGetProducts() {
			return getProducts;
		}
		public void setGetProducts(List<ProductCondition> getProducts) {
			this.getProducts = getProducts;
		}
		public int getRepetitionLimit() {
			return repetitionLimit;
		}
		public void setRepetitionLimit(int repetitionLimit) {
			this.repetitionLimit = repetitionLimit;
		}
		@Override
		public String toString() {
			return "CouponDetails [threshold=" + threshold + ", discount=" + discount + ", buyProducts=" + buyProducts
					+ ", getProducts=" + getProducts + ", repetitionLimit=" + repetitionLimit + "]";
		}
		public CouponDetails(double threshold, double discount, List<ProductCondition> buyProducts,
				List<ProductCondition> getProducts, int repetitionLimit) {
			super();
			this.threshold = threshold;
			this.discount = discount;
			this.buyProducts = buyProducts;
			this.getProducts = getProducts;
			this.repetitionLimit = repetitionLimit;
		}
		public CouponDetails() {
			super();
			
		}
	 
	}

	

