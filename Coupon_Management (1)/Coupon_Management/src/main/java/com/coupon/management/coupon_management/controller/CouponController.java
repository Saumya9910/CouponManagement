package com.coupon.management.coupon_management.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.coupon.management.coupon_management.entity.*;
import com.coupon.management.coupon_management.repository.ProductRepo;
import com.coupon.management.coupon_management.service.CouponService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/coupons")
public class CouponController {

	@Autowired
	private CouponService couponService;

	@Autowired
	private ProductRepo productRepository;

	
	@PostMapping
	public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
		Coupon savedCoupon = couponService.createCoupon(coupon);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCoupon);
	}

	
	@GetMapping
	public List<Coupon> getAllCoupons() {
		return couponService.getAllCoupons();
	}

	
	@GetMapping("/{id}")
	public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
		return couponService.getCouponById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	
	@PutMapping("/{id}")
	public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon updatedCoupon) {
		return ResponseEntity.ok(couponService.updateCoupon(id, updatedCoupon));
	}

	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
		couponService.deleteCoupon(id);
		return ResponseEntity.noContent().build();
	}

	
	@PostMapping("/applicable-coupons")
	public ResponseEntity<?> getApplicableCoupons(@RequestBody Cart cart) {
		List<Coupon> allCoupons = couponService.getAllCoupons();
		List<Map<String, Object>> applicableCoupons = new ArrayList<>();

		for (Coupon coupon : allCoupons) {
			double discount = 0;

			switch (coupon.getType()) {
			case "cart-wise":
				if (cart.getTotalPrice() > coupon.getDetails().getThreshold()) {
					discount = cart.getTotalPrice() * coupon.getDetails().getDiscount() / 100;
				}
				break;
			case "product-wise":
				for (CartItem item : cart.getItems()) {
					Optional<Product> productOpt = productRepository.findById(item.getProductId());
					if (productOpt.isPresent() && coupon.getDetails().getBuyProducts().stream()
							.anyMatch(p -> p.getProductId().equals(item.getProductId()))) {
						double price = productOpt.get().getPrice();
						double itemDiscount = price * item.getQuantity() * coupon.getDetails().getDiscount() / 100;
						item.setPrice(price - (itemDiscount / item.getQuantity()));
						discount += itemDiscount;
					}
				}
				break;

			case "bxgy":
				discount = calculateBxgyDiscount(coupon, cart);
				break;

			default:
				break;
			}

			if (discount > 0) {
				Map<String, Object> couponInfo = new HashMap<>();
				couponInfo.put("coupon_id", coupon.getId());
				couponInfo.put("type", coupon.getType());
				couponInfo.put("discount", discount);
				applicableCoupons.add(couponInfo);
			}
		}

		return ResponseEntity.ok(Map.of("applicable_coupons", applicableCoupons));
	}

	private double calculateBxgyDiscount(Coupon coupon, Cart cart) {
		double totalDiscount = 0;

		// Get the buy and get product details and repetition limit from the coupon
		List<ProductCondition> buyProducts = coupon.getDetails().getBuyProducts();
		List<ProductCondition> getProducts = coupon.getDetails().getGetProducts();
		int repetitionLimit = coupon.getDetails().getRepetitionLimit();

		
		Map<Long, CartItem> cartItemMap = cart.getItems().stream()
				.collect(Collectors.toMap(CartItem::getProductId, item -> item));

		
		int maxApplicableTimes = Integer.MAX_VALUE;	
		for (ProductCondition buyProduct : buyProducts) {
			CartItem cartItem = cartItemMap.get(buyProduct.getProductId());
			if (cartItem == null || cartItem.getQuantity() < buyProduct.getQuantity()) {
				return 0; 
			}
			maxApplicableTimes = Math.min(maxApplicableTimes, cartItem.getQuantity() / buyProduct.getQuantity());
		}	
		int applicableTimes = Math.min(maxApplicableTimes, repetitionLimit);
		for (ProductCondition getProduct : getProducts) {
			CartItem getItem = cartItemMap.get(getProduct.getProductId());

			if (getItem != null) {
				// Apply discount only on the quantity that qualifies as "free" or discounted
				int eligibleDiscountQuantity = Math.min(getItem.getQuantity(),
						getProduct.getQuantity() * applicableTimes);
				double discountPerItem = getItem.getPrice(); // Assuming full price discount per free item

				totalDiscount += discountPerItem * eligibleDiscountQuantity;
			}
		}

		return totalDiscount;
	}

	
	@PostMapping("/apply-coupon/{id}")
	public ResponseEntity<?> applyCouponToCart(@PathVariable Long id, @RequestBody Cart cart) {
		Optional<Coupon> optionalCoupon = couponService.getCouponById(id);

		if (optionalCoupon.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coupon not found");
		}
		Coupon coupon = optionalCoupon.get();
		if (coupon.getExpirationDate() == null) {
			coupon.setExpirationDate(LocalDateTime.now().plusDays(30)); // Set default expiration if missing
		}
		double totalDiscount = 0;

		for (CartItem item : cart.getItems()) {
			if (productRepository.findById(item.getProductId()).isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Product with ID " + item.getProductId() + " does not exist");
			}
		}

		
		switch (coupon.getType()) {
		case "cart-wise":
			if (cart.getTotalPrice() > coupon.getDetails().getThreshold()) {
				totalDiscount = cart.getTotalPrice() * coupon.getDetails().getDiscount() / 100;

				double remainingDiscount = totalDiscount;

				for (CartItem item : cart.getItems()) {
					double itemDiscount = (item.getPrice() / cart.getTotalPrice()) * totalDiscount;
					item.setDiscount(itemDiscount);
					remainingDiscount -= itemDiscount;
				}
				double finalPrice = cart.getTotalPrice() - totalDiscount;
				Map<String, Object> response = new HashMap<>();
				response.put("total_discount", totalDiscount);
				response.put("total_price", cart.getTotalPrice());
				response.put("final_price", finalPrice);
				response.put("updated_cart", cart);

				return ResponseEntity.ok(response);
			}
			break;

		case "product-wise":
			for (CartItem item : cart.getItems()) {
				Optional<Product> productOpt = productRepository.findById(item.getProductId());
				if (productOpt.isPresent() && coupon.getDetails().getBuyProducts().stream()
						.anyMatch(p -> p.getProductId().equals(item.getProductId()))) {

					double price = item.getPrice();
					double itemDiscount = price * item.getDiscount() / 100 * item.getQuantity();
					item.setDiscount(itemDiscount);
					totalDiscount += itemDiscount;
				}
			}
			break;

		case "bxgy":
			totalDiscount = calculateBxgyDiscount(coupon, cart);

			break;

		default:
			break;
		}

		double originalTotalPrice = cart.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity())
				.sum();
		double updatedTotalPrice = originalTotalPrice - totalDiscount;
		cart.setTotalPrice(updatedTotalPrice);
		double finalPrice = updatedTotalPrice;

		Map<String, Object> response = new HashMap<>();
		response.put("updated_cart", cart);
		response.put("total_price", originalTotalPrice);
		response.put("total_discount", totalDiscount);
		response.put("final_price", finalPrice);

		return ResponseEntity.ok(response);
	}

}
