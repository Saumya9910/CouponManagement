
# Coupons Management API for an E-commerce Website

The Coupon Management API provides an interface for creating, applying, and managing coupons in an e-commerce environment. This API supports various types of discount strategies, including cart-wide, product-specific, and "Buy X Get Y" (BxGy) promotions.


## API Reference

#### Create Coupon

```http
  POST /coupons
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `string` | **Required**. Your Id |
| `type` | `string` | Which type of coupon |
| `details` | `string[]` | Details of the coupon (see below) |
| `expirationDate` | `string` |here is your expiration date which is of 30 days |

#### Get All Coupons
```http
  GET /coupons
```

#### Get Coupon By Id

```http
  GET /coupons/{id}
```

| PathVariable | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `string` | **Required**. Your Id |

#### Update Coupon By Id

```http
  PUT /coupons/{id}

```
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `type` | `string` | Which type of coupon |
| `details` | `string[]` | Details of the coupon (see below) |
| `expirationDate` | `string` |here is your expiration date which is of 30 days |

Coupon Id would be pass in PathVariable

```json
"details": {
    "threshold": 100,
    "discount": 10,
    "buyProducts": [
        {
            "productId": 1,
            "quantity": 2
        },
        {
            "productId": 2,
            "quantity": 3
        }
    ],
    "getProducts": [
        {
            "productId": 3,
            "quantity": 1
        }
    ],
    "repetitionLimit": 3
}
```
#### Delete Coupon By Id
######  Deletes a coupon by its ID.

```http
  DELETE /coupons

```
| PathVariable | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `id` | `string` | **Required**. Your Id |

#### Applicable Coupons
######  Returns a list of applicable coupons based on the cart's contents.
```http
  POST /coupons/applicable-coupons

```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `totalPrice` | `string` |total price of coupon  |
| `items` | `string[]` | items of coupon (see below)|
```json
{
    "items": [
        {
            "productId": 1,
            "quantity": 1,
            "price": 9000
        },
        {
            "productId": 3,
            "quantity": 3,
            "price": 1000
        },
        {
            "productId": 2,
            "quantity": 1,
            "price": 9000
        }
    ],
    "totalPrice": 21000
}
```http
  POST /coupons/apply-coupon/{id}

```
#### Apply Coupons By Id
######  Applies a specified coupon to the cart and returns the updated cart details.
| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `totalPrice` | `string` |total price of coupon  |
| `items` | `string[]` | items of coupon (see below)|

Here Coupon Id  pass in PathVariable

```json
{
    "items": [
        {
            "productId": 1,
            "quantity": 1,
            "price": 9000
        },
        {
            "productId": 3,
            "quantity": 3,
            "price": 1000
        },
        {
            "productId": 2,
            "quantity": 1,
            "price": 9000
        }
    ],
    "totalPrice": 21000
}

```





## Demo

Git Link

https://github.com/Saumya9910/CouponManagement
## Usage/Examples

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

```




## Implemented Cases

1) Cart-Wide Discount:

a) Applies a percentage discount if the total cart value exceeds a specified threshold.

b) Discount is allocated proportionally across cart items.

2) Product-Specific Discount:

a) Provides a discount on specified products within a cart.

b) Discounts are applied only to matching items in the cart as per coupon criteria.

3) Buy X Get Y Free (BxGy) Discount:

a) Provides a free or discounted quantity of a specified product when the required quantity of another product is bought.

b) Discounts are capped by a repetition limit for how many times the offer can be applied.
## UnImplemented Cases

1)Complex Combined Discounts:

a) Cases combining multiple discount types within a single coupon are not yet implemented.

2)Tiered Discounts:

b) Coupons that apply different discount percentages based on incremental thresholds (e.g., spend $100 for 10% off, $200 for 20% off).

3)Time-Limited Coupons:

a) Coupons that activate only within specific date and time ranges have not been implemented.

## Limitations

1) Fixed Repetition Limit in BxGy Discounts:

a) The repetition limit is predefined in the coupon creation. Dynamic repetition limits based on cart conditions are not supported.

2) Dependency on Product Availability:

a) Discounts cannot be applied to items not currently available in the product repository. Coupons referring to such products will raise an error.

3) Flat Discount Rates Only:

a) The API supports only flat percentage discounts and free-item discounts. Custom discount rules are unsupported.

### Assumptions
1) Default Expiration Date:

a) If no expiration date is provided for a coupon, it is automatically set to expire 30 days from creation.

2) Full Price Applied to Free Items in BxGy:

a) When calculating "Buy X Get Y" offers, the "Y" items are assumed to be fully discounted.

3) Discounted Price Applies After Discount Validation:

a) Discounts are applied only if all specified conditions are met; partial conditions do not yield partial discounts.

4) Product Existence Check:

a) During coupon creation, all referenced products are assumed to exist in the ProductRepo. Missing products trigger an InvalidProductException.


