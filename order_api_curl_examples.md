# Order API cURL Examples

This file contains cURL examples for testing the Order Management APIs.

## Prerequisites

1. Make sure the application is running on `http://localhost:8080`
2. Replace `YOUR_JWT_TOKEN` with a valid JWT token for a vendor user
3. The JWT token should have the VENDOR role

## Authentication

First, you need to get a JWT token. Use the authentication endpoint:

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vendor@example.com",
    "password": "password123"
  }'
```

## 1. Get All Orders

### Get all orders (default pagination)

```bash
curl -X GET "http://localhost:8080/api/orders" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get orders with filters

```bash
curl -X GET "http://localhost:8080/api/orders?orderStatus=PENDING&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get orders by date range

```bash
curl -X GET "http://localhost:8080/api/orders?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Search orders

```bash
curl -X GET "http://localhost:8080/api/orders?searchTerm=john" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get orders by payment status

```bash
curl -X GET "http://localhost:8080/api/orders?paymentStatus=PAID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get orders by order type

```bash
curl -X GET "http://localhost:8080/api/orders?orderType=PICKUP" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 2. Get Order by ID

```bash
curl -X GET "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 3. Update Order Status

### Update to CONFIRMED

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "CONFIRMED",
    "notes": "Order confirmed and preparation started",
    "estimatedPreparationTime": 25
  }'
```

### Update to PREPARING

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "PREPARING",
    "notes": "Order is now being prepared"
  }'
```

### Update to READY

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "READY",
    "notes": "Order is ready for pickup",
    "actualPreparationTime": 22
  }'
```

### Update to COMPLETED

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "COMPLETED",
    "notes": "Order has been completed successfully"
  }'
```

### Cancel order

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "CANCELLED",
    "notes": "Order cancelled due to item unavailability"
  }'
```

## 4. Get Order History

### Get all order history

```bash
curl -X GET "http://localhost:8080/api/orders/history" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get completed orders history

```bash
curl -X GET "http://localhost:8080/api/orders/history?orderStatus=COMPLETED" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get order history by date range

```bash
curl -X GET "http://localhost:8080/api/orders/history?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 5. Get Order by Order Number

```bash
curl -X GET "http://localhost:8080/api/orders/number/ORD-2024-001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Error Testing Examples

### Test with invalid order ID

```bash
curl -X GET "http://localhost:8080/api/orders/999" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Test with invalid order number

```bash
curl -X GET "http://localhost:8080/api/orders/number/INVALID-ORDER" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Test with invalid order status

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "INVALID_STATUS"
  }'
```

### Test without authentication

```bash
curl -X GET "http://localhost:8080/api/orders"
```

### Test with invalid JWT token

```bash
curl -X GET "http://localhost:8080/api/orders" \
  -H "Authorization: Bearer INVALID_TOKEN"
```

## Complete Workflow Example

Here's a complete workflow example showing the typical order lifecycle:

### 1. Get pending orders

```bash
curl -X GET "http://localhost:8080/api/orders?orderStatus=PENDING" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Confirm an order

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "CONFIRMED",
    "notes": "Order confirmed",
    "estimatedPreparationTime": 20
  }'
```

### 3. Start preparing the order

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "PREPARING",
    "notes": "Order is being prepared"
  }'
```

### 4. Mark order as ready

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "READY",
    "notes": "Order is ready for pickup",
    "actualPreparationTime": 18
  }'
```

### 5. Complete the order

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderStatus": "COMPLETED",
    "notes": "Order completed successfully"
  }'
```

### 6. Check order history

```bash
curl -X GET "http://localhost:8080/api/orders/history?orderStatus=COMPLETED" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Enhanced Order Management Examples

### 6. Get Pending Orders

```bash
curl -X GET "http://localhost:8080/api/orders/pending?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 7. Get Preparing Orders

```bash
curl -X GET "http://localhost:8080/api/orders/preparing?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 8. Get Ready Orders

```bash
curl -X GET "http://localhost:8080/api/orders/ready?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 9. Get Completed Orders

```bash
curl -X GET "http://localhost:8080/api/orders/completed?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 10. Get Cancelled Orders

```bash
curl -X GET "http://localhost:8080/api/orders/cancelled?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Order Action Examples

### 11. Accept Order

```bash
curl -X POST "http://localhost:8080/api/orders/1/accept" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Order accepted and preparation will start soon",
    "estimatedPreparationTime": 25
  }'
```

### 12. Reject Order

```bash
curl -X POST "http://localhost:8080/api/orders/1/reject" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Item not available",
    "notes": "Customer will be notified"
  }'
```

### 13. Start Preparing Order

```bash
curl -X POST "http://localhost:8080/api/orders/1/start-preparing" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Preparation started",
    "estimatedPreparationTime": 20
  }'
```

### 14. Mark Order as Ready

```bash
curl -X POST "http://localhost:8080/api/orders/1/ready" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Order is ready for pickup",
    "actualPreparationTime": 18
  }'
```

### 15. Complete Order

```bash
curl -X POST "http://localhost:8080/api/orders/1/complete" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Order completed successfully"
  }'
```

## Search and Filter Examples

### 16. Search Orders

```bash
curl -X GET "http://localhost:8080/api/orders/search?searchTerm=john&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 17. Filter Orders

```bash
curl -X GET "http://localhost:8080/api/orders/filter?orderStatus=PENDING&paymentStatus=PENDING&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Enhanced Complete Workflow Example

Here's a complete workflow using the new action endpoints:

### 1. Get pending orders

```bash
curl -X GET "http://localhost:8080/api/orders/pending" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 2. Accept an order

```bash
curl -X POST "http://localhost:8080/api/orders/1/accept" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Order accepted",
    "estimatedPreparationTime": 20
  }'
```

### 3. Start preparing the order

```bash
curl -X POST "http://localhost:8080/api/orders/1/start-preparing" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Order is being prepared",
    "estimatedPreparationTime": 20
  }'
```

### 4. Mark order as ready

```bash
curl -X POST "http://localhost:8080/api/orders/1/ready" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Order is ready for pickup",
    "actualPreparationTime": 18
  }'
```

### 5. Complete the order

```bash
curl -X POST "http://localhost:8080/api/orders/1/complete" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Order completed successfully"
  }'
```

### 6. Check completed orders

```bash
curl -X GET "http://localhost:8080/api/orders/completed" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Notes

- All timestamps should be in ISO format (YYYY-MM-DDTHH:mm:ss)
- The `page` parameter is 0-based (0 = first page)
- Order status values are case-sensitive: PENDING, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED
- Payment status values: PENDING, PAID, FAILED, REFUNDED
- Order type values: PICKUP, DINE_IN
- Sort direction values: ASC, DESC
- Order actions follow a specific workflow: PENDING → CONFIRMED → PREPARING → READY → COMPLETED
- Rejection changes status directly from PENDING to CANCELLED
