# Order Management API Documentation

This document describes the Order Management APIs for the Crave Kitchen Portal.

## Base URL

```
http://localhost:8080/api/orders
```

## Authentication

All endpoints require JWT authentication with VENDOR role.

## API Endpoints

### 1. Get All Orders

**GET** `/api/orders`

Retrieves a paginated list of orders with optional filtering.

#### Query Parameters

| Parameter       | Type     | Required | Description                                                                         |
| --------------- | -------- | -------- | ----------------------------------------------------------------------------------- |
| `orderStatus`   | String   | No       | Filter by order status (PENDING, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED) |
| `paymentStatus` | String   | No       | Filter by payment status (PENDING, PAID, FAILED, REFUNDED)                          |
| `orderType`     | String   | No       | Filter by order type (PICKUP, DINE_IN)                                              |
| `startDate`     | DateTime | No       | Filter orders created after this date (ISO format)                                  |
| `endDate`       | DateTime | No       | Filter orders created before this date (ISO format)                                 |
| `searchTerm`    | String   | No       | Search in order number, customer name, or email                                     |
| `orderNumber`   | String   | No       | Filter by specific order number                                                     |
| `customerId`    | Long     | No       | Filter by customer ID                                                               |
| `page`          | Integer  | No       | Page number (default: 0)                                                            |
| `size`          | Integer  | No       | Page size (default: 20)                                                             |
| `sortBy`        | String   | No       | Sort field (default: createdAt)                                                     |
| `sortDirection` | String   | No       | Sort direction: ASC or DESC (default: DESC)                                         |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders?orderStatus=PENDING&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Example Response

```json
{
  "success": true,
  "message": "Orders retrieved successfully",
  "timestamp": "2024-01-15T10:30:00",
  "data": {
    "content": [
      {
        "id": 1,
        "orderNumber": "ORD-2024-001",
        "customerId": 123,
        "customerName": "John Doe",
        "customerEmail": "john@example.com",
        "vendorId": 456,
        "vendorName": "Tasty Kitchen",
        "orderStatus": "PENDING",
        "orderType": "PICKUP",
        "totalAmount": 25.5,
        "subtotalAmount": 23.0,
        "taxAmount": 2.5,
        "tipAmount": 0.0,
        "discountAmount": 0.0,
        "paymentStatus": "PENDING",
        "paymentMethod": "CARD",
        "estimatedPreparationTime": 20,
        "actualPreparationTime": null,
        "pickupTime": "2024-01-15T11:00:00",
        "tableNumber": null,
        "orderNotes": "Extra spicy please",
        "customerNotes": "Please call when ready",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true
  }
}
```

### 2. Get Order by ID

**GET** `/api/orders/{id}`

Retrieves a specific order by its ID.

#### Path Parameters

| Parameter | Type | Required | Description |
| --------- | ---- | -------- | ----------- |
| `id`      | Long | Yes      | Order ID    |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Example Response

```json
{
  "success": true,
  "message": "Order retrieved successfully",
  "timestamp": "2024-01-15T10:30:00",
  "data": {
    "id": 1,
    "orderNumber": "ORD-2024-001",
    "customerId": 123,
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "vendorId": 456,
    "vendorName": "Tasty Kitchen",
    "orderStatus": "PENDING",
    "orderType": "PICKUP",
    "totalAmount": 25.5,
    "subtotalAmount": 23.0,
    "taxAmount": 2.5,
    "tipAmount": 0.0,
    "discountAmount": 0.0,
    "paymentStatus": "PENDING",
    "paymentMethod": "CARD",
    "estimatedPreparationTime": 20,
    "actualPreparationTime": null,
    "pickupTime": "2024-01-15T11:00:00",
    "tableNumber": null,
    "orderNotes": "Extra spicy please",
    "customerNotes": "Please call when ready",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

### 3. Update Order Status

**PUT** `/api/orders/{id}`

Updates the status of a specific order.

#### Path Parameters

| Parameter | Type | Required | Description |
| --------- | ---- | -------- | ----------- |
| `id`      | Long | Yes      | Order ID    |

#### Request Body

```json
{
  "orderStatus": "CONFIRMED",
  "notes": "Order confirmed and preparation started",
  "estimatedPreparationTime": 25,
  "actualPreparationTime": null
}
```

| Field                      | Type    | Required | Description                                                                   |
| -------------------------- | ------- | -------- | ----------------------------------------------------------------------------- |
| `orderStatus`              | String  | Yes      | New order status (PENDING, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED) |
| `notes`                    | String  | No       | Additional notes about the status change                                      |
| `estimatedPreparationTime` | Integer | No       | Estimated preparation time in minutes                                         |
| `actualPreparationTime`    | Integer | No       | Actual preparation time in minutes                                            |

#### Example Request

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

#### Example Response

```json
{
  "success": true,
  "message": "Order status updated successfully",
  "timestamp": "2024-01-15T10:35:00",
  "data": {
    "id": 1,
    "orderNumber": "ORD-2024-001",
    "customerId": 123,
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "vendorId": 456,
    "vendorName": "Tasty Kitchen",
    "orderStatus": "CONFIRMED",
    "orderType": "PICKUP",
    "totalAmount": 25.5,
    "subtotalAmount": 23.0,
    "taxAmount": 2.5,
    "tipAmount": 0.0,
    "discountAmount": 0.0,
    "paymentStatus": "PENDING",
    "paymentMethod": "CARD",
    "estimatedPreparationTime": 25,
    "actualPreparationTime": null,
    "pickupTime": "2024-01-15T11:00:00",
    "tableNumber": null,
    "orderNotes": "Extra spicy please",
    "customerNotes": "Please call when ready",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:35:00"
  }
}
```

### 4. Get Order History

**GET** `/api/orders/history`

Retrieves order history with optional filtering.

#### Query Parameters

Same as "Get All Orders" endpoint.

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/history?orderStatus=COMPLETED&startDate=2024-01-01T00:00:00" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Example Response

```json
{
  "success": true,
  "message": "Order history retrieved successfully",
  "timestamp": "2024-01-15T10:30:00",
  "data": [
    {
      "id": 1,
      "orderNumber": "ORD-2024-001",
      "customerId": 123,
      "customerName": "John Doe",
      "customerEmail": "john@example.com",
      "vendorId": 456,
      "vendorName": "Tasty Kitchen",
      "orderStatus": "COMPLETED",
      "orderType": "PICKUP",
      "totalAmount": 25.5,
      "subtotalAmount": 23.0,
      "taxAmount": 2.5,
      "tipAmount": 0.0,
      "discountAmount": 0.0,
      "paymentStatus": "PAID",
      "paymentMethod": "CARD",
      "estimatedPreparationTime": 25,
      "actualPreparationTime": 22,
      "pickupTime": "2024-01-15T11:00:00",
      "tableNumber": null,
      "orderNotes": "Extra spicy please",
      "customerNotes": "Please call when ready",
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T11:00:00"
    }
  ]
}
```

### 5. Get Order by Order Number

**GET** `/api/orders/number/{orderNumber}`

Retrieves a specific order by its order number.

#### Path Parameters

| Parameter     | Type   | Required | Description                         |
| ------------- | ------ | -------- | ----------------------------------- |
| `orderNumber` | String | Yes      | Order number (e.g., "ORD-2024-001") |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/number/ORD-2024-001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Example Response

Same as "Get Order by ID" endpoint.

## Enhanced Order Management Endpoints

### 6. Get Pending Orders

**GET** `/api/orders/pending`

Retrieves only pending orders with pagination.

#### Query Parameters

| Parameter | Type    | Required | Description              |
| --------- | ------- | -------- | ------------------------ |
| `page`    | Integer | No       | Page number (default: 0) |
| `size`    | Integer | No       | Page size (default: 20)  |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/pending?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 7. Get Preparing Orders

**GET** `/api/orders/preparing`

Retrieves only orders being prepared with pagination.

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/preparing?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 8. Get Ready Orders

**GET** `/api/orders/ready`

Retrieves only ready orders with pagination.

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/ready?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 9. Get Completed Orders

**GET** `/api/orders/completed`

Retrieves only completed orders with pagination.

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/completed?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 10. Get Cancelled Orders

**GET** `/api/orders/cancelled`

Retrieves only cancelled orders with pagination.

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/cancelled?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Order Action Endpoints

### 11. Accept Order

**POST** `/api/orders/{id}/accept`

Accepts a pending order and changes its status to CONFIRMED.

#### Path Parameters

| Parameter | Type | Required | Description |
| --------- | ---- | -------- | ----------- |
| `id`      | Long | Yes      | Order ID    |

#### Request Body

```json
{
  "notes": "Order accepted and preparation will start soon",
  "estimatedPreparationTime": 25
}
```

| Field                      | Type    | Required | Description                           |
| -------------------------- | ------- | -------- | ------------------------------------- |
| `notes`                    | String  | No       | Additional notes about the acceptance |
| `estimatedPreparationTime` | Integer | No       | Estimated preparation time in minutes |

#### Example Request

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

**POST** `/api/orders/{id}/reject`

Rejects a pending order and changes its status to CANCELLED.

#### Request Body

```json
{
  "reason": "Item not available",
  "notes": "Customer will be notified"
}
```

| Field    | Type   | Required | Description          |
| -------- | ------ | -------- | -------------------- |
| `reason` | String | Yes      | Reason for rejection |
| `notes`  | String | No       | Additional notes     |

#### Example Request

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

**POST** `/api/orders/{id}/start-preparing`

Starts preparing a confirmed order and changes its status to PREPARING.

#### Request Body

```json
{
  "notes": "Preparation started",
  "estimatedPreparationTime": 20
}
```

| Field                      | Type    | Required | Description                           |
| -------------------------- | ------- | -------- | ------------------------------------- |
| `notes`                    | String  | No       | Additional notes                      |
| `estimatedPreparationTime` | Integer | No       | Estimated preparation time in minutes |

#### Example Request

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

**POST** `/api/orders/{id}/ready`

Marks a preparing order as ready and changes its status to READY.

#### Request Body

```json
{
  "notes": "Order is ready for pickup",
  "actualPreparationTime": 18
}
```

| Field                   | Type    | Required | Description                        |
| ----------------------- | ------- | -------- | ---------------------------------- |
| `notes`                 | String  | No       | Additional notes                   |
| `actualPreparationTime` | Integer | No       | Actual preparation time in minutes |

#### Example Request

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

**POST** `/api/orders/{id}/complete`

Marks a ready order as completed and changes its status to COMPLETED.

#### Request Body

```json
{
  "notes": "Order completed successfully"
}
```

| Field   | Type   | Required | Description      |
| ------- | ------ | -------- | ---------------- |
| `notes` | String | No       | Additional notes |

#### Example Request

```bash
curl -X POST "http://localhost:8080/api/orders/1/complete" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Order completed successfully"
  }'
```

## Search and Filter Endpoints

### 16. Search Orders

**GET** `/api/orders/search`

Searches orders by term (order number, customer name, email).

#### Query Parameters

| Parameter    | Type    | Required | Description              |
| ------------ | ------- | -------- | ------------------------ |
| `searchTerm` | String  | Yes      | Search term              |
| `page`       | Integer | No       | Page number (default: 0) |
| `size`       | Integer | No       | Page size (default: 20)  |

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/search?searchTerm=john&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 17. Filter Orders

**GET** `/api/orders/filter`

Filters orders by multiple criteria with pagination.

#### Query Parameters

Same as "Get All Orders" endpoint.

#### Example Request

```bash
curl -X GET "http://localhost:8080/api/orders/filter?orderStatus=PENDING&paymentStatus=PENDING&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Error Responses

### 404 Not Found

```json
{
  "success": false,
  "message": "Order not found",
  "error": "Order not found with ID: 999",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 403 Forbidden

```json
{
  "success": false,
  "message": "Order not found or access denied",
  "error": "Order does not belong to vendor: 456",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 400 Bad Request

```json
{
  "success": false,
  "message": "Validation failed",
  "error": "VALIDATION_ERROR",
  "timestamp": "2024-01-15T10:30:00",
  "errors": [
    {
      "field": "orderStatus",
      "message": "Order status is required",
      "code": "NOT_NULL"
    }
  ]
}
```

### 500 Internal Server Error

```json
{
  "success": false,
  "message": "Failed to retrieve orders",
  "error": "Database connection error",
  "timestamp": "2024-01-15T10:30:00"
}
```

## Order Status Flow

The order status follows this progression:

1. **PENDING** - Order received, awaiting confirmation
2. **CONFIRMED** - Order confirmed by vendor, preparation can begin
3. **PREPARING** - Order is being prepared
4. **READY** - Order is ready for pickup/delivery
5. **COMPLETED** - Order has been completed
6. **CANCELLED** - Order has been cancelled

## Payment Status

- **PENDING** - Payment not yet processed
- **PAID** - Payment completed successfully
- **FAILED** - Payment failed
- **REFUNDED** - Payment has been refunded

## Order Types

- **PICKUP** - Customer will pick up the order
- **DINE_IN** - Order for dine-in service

## Security Notes

- All endpoints require JWT authentication
- Vendors can only access orders belonging to their restaurant
- Order status changes are logged in the order status history
- All operations are audited for security and compliance
