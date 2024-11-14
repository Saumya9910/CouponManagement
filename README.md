# CouponManagement

The Coupon Management API provides an interface for creating, applying, and managing coupons in an e-commerce environment. This API supports various types of discount strategies, including cart-wide, product-specific, and "Buy X Get Y" (BxGy) promotions.

Table of Contents
Implemented Cases
Unimplemented Cases
Limitations
Assumptions
Endpoints and Usage
Create Coupon
Get All Coupons
Get Coupon by ID
Update Coupon
Delete Coupon
Retrieve Applicable Coupons for Cart
Apply Coupon to Cart
Sample Requests and Responses
Implemented Cases
Cart-Wide Discount:

Applies a percentage discount if the total cart value exceeds a specified threshold.
Discount is allocated proportionally across cart items.
Product-Specific Discount:

Provides a discount on specified products within a cart.
Discounts are applied only to matching items in the cart as per coupon criteria.
Buy X Get Y Free (BxGy) Discount:

Provides a free or discounted quantity of a specified product when the required quantity of another product is bought.
Discounts are capped by a repetition limit for how many times the offer can be applied.
Unimplemented Cases
Complex Combined Discounts:
Cases combining multiple discount types within a single coupon are not yet implemented.
Tiered Discounts:
Coupons that apply different discount percentages based on incremental thresholds (e.g., spend $100 for 10% off, $200 for 20% off).
Time-Limited Coupons:
Coupons that activate only within specific date and time ranges have not been implemented.
Limitations
Fixed Repetition Limit in BxGy Discounts:
The repetition limit is predefined in the coupon creation. Dynamic repetition limits based on cart conditions are not supported.
Dependency on Product Availability:
Discounts cannot be applied to items not currently available in the product repository. Coupons referring to such products will raise an error.
Flat Discount Rates Only:
The API supports only flat percentage discounts and free-item discounts. Custom discount rules are unsupported.
Assumptions
Default Expiration Date:
If no expiration date is provided for a coupon, it is automatically set to expire 30 days from creation.
Full Price Applied to Free Items in BxGy:
When calculating "Buy X Get Y" offers, the "Y" items are assumed to be fully discounted.
Discounted Price Applies After Discount Validation:
Discounts are applied only if all specified conditions are met; partial conditions do not yield partial discounts.
Product Existence Check:
During coupon creation, all referenced products are assumed to exist in the ProductRepo. Missing products trigger an InvalidProductException.
Endpoints and Usage
Create Coupon
Endpoint: POST /coupons
Description: Creates a new coupon with specified discount conditions.
Request:
json
Copy code
{
  "type": "bxgy",
  "details": {
    "threshold": 200,
    "discount": 10,
    "buyProducts": [
      { "productId": 1, "quantity": 2 }
    ],
    "getProducts": [
      { "productId": 2, "quantity": 1 }
    ],
    "repetitionLimit": 3
  },
  "expirationDate": "2024-12-31T23:59:59"
}
Response:
json
Copy code
{
  "id": 1,
  "type": "bxgy",
  "details": {
    "threshold": 200,
    "discount": 10,
    "buyProducts": [
      { "productId": 1, "quantity": 2 } ],
    "getProducts": [
      { "productId": 2, "quantity": 1 }
    ],
    "repetitionLimit": 3
  },
  "expirationDate": "2024-12-31T23:59:59"
}
Get All Coupons
Endpoint: GET /coupons
Description: Retrieves all available coupons.
Response:
json
Copy code
[
  {
    "id": 1,
    "type": "cart-wise",
    "details": { "threshold": 200, "discount": 15 }
  },
  ...
]
Get Coupon by ID
Endpoint: GET /coupons/{id}
Description: Retrieves a specific coupon by its ID.
Response:
json
Copy code
{
  "id": 1,
  "type": "bxgy",
  "details": {
    "buyProducts": [...],
    "getProducts": [...],
    "repetitionLimit": 3
  }
}
Update Coupon
Endpoint: PUT /coupons/{id}
Description: Updates details of an existing coupon.
Request: Similar to Create Coupon
Delete Coupon
Endpoint: DELETE /coupons/{id}
Description: Deletes a coupon by its ID.
Retrieve Applicable Coupons for Cart
Endpoint: POST /coupons/applicable-coupons
Description: Returns a list of applicable coupons based on the cart's contents.
Request:
json
Copy code
{
  "items": [
    { "productId": 1, "quantity": 3 },
    { "productId": 2, "quantity": 1 }
  ],
  "totalPrice": 250
}
Response:
json
Copy code
{
  "applicable_coupons": [
    { "coupon_id": 1, "type": "cart-wise", "discount": 37.5 },
    ...
  ]
}
Apply Coupon to Cart
Endpoint: POST /coupons/apply-coupon/{id}
Description: Applies a specified coupon to the cart and returns the updated cart details.
Request:
json
Copy code
{
  "items": [
    { "productId": 1, "quantity": 3, "price": 100 },
    { "productId": 2, "quantity": 2, "price": 50 }
  ],
  "totalPrice": 250
}
Response:
json
Copy code
{
  "updated_cart": {
    "items": [
      { "productId": 1, "quantity": 3, "price": 90, "discount": 30 },
      { "productId": 2, "quantity": 2, "price": 50, "discount": 10 }
    ],
    "total_price": 250,
    "total_discount": 40,
    "final_price": 210
  }
}
This document provides a comprehensive overview of the Coupon Management API, detailing supported discount cases, limitations, assumptions, and sample JSON requests and responses for each endpoint.






