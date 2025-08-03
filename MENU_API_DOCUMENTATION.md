# Menu Management API Documentation

## Overview

The Menu Management API provides comprehensive functionality for managing restaurant menus, including categories, menu items, images, and availability. All database tables follow the "CK\_" naming convention.

## Database Tables

The following tables have been created with the CK\_ prefix:

- `ck_menu_categories` - Menu categories (Appetizers, Main Course, etc.)
- `ck_menu_items` - Individual menu items/dishes
- `ck_menu_item_images` - Images associated with menu items
- `ck_menu_item_availability` - Availability schedules for menu items

## API Endpoints

### Base URL

```
/api/v1/menu
```

### Authentication

Most endpoints require authentication with the `VENDOR` role for write operations. Read operations are generally public.

### 1. Menu Overview

#### Get Menu Overview

```
GET /api/v1/menu/overview/{vendorId}
```

Returns a comprehensive overview of the vendor's menu including statistics and featured items.

**Response:**

```json
{
  "success": true,
  "message": "Menu overview retrieved successfully",
  "data": {
    "vendorId": 1,
    "vendorName": "Test Restaurant",
    "totalCategories": 5,
    "activeCategories": 4,
    "totalItems": 20,
    "availableItems": 18,
    "featuredItems": 3,
    "vegetarianItems": 8,
    "veganItems": 4,
    "glutenFreeItems": 6,
    "categories": [...],
    "featuredItemsList": [...],
    "recentItems": [...]
  }
}
```

### 2. Menu Categories

#### Create Menu Category

```
POST /api/v1/menu/categories/{vendorId}
```

**Requires:** VENDOR role

**Request Body:**

```json
{
  "name": "Appetizers",
  "description": "Start your meal with our delicious appetizers",
  "displayOrder": 1,
  "isActive": true,
  "isFeatured": false,
  "imageUrl": "https://example.com/appetizers.jpg"
}
```

#### Update Menu Category

```
PUT /api/v1/menu/categories/{vendorId}/{categoryId}
```

**Requires:** VENDOR role

#### Get Menu Category

```
GET /api/v1/menu/categories/{vendorId}/{categoryId}
```

#### Get All Menu Categories

```
GET /api/v1/menu/categories/{vendorId}?activeOnly=false
```

#### Get Featured Menu Categories

```
GET /api/v1/menu/categories/{vendorId}/featured
```

#### Delete Menu Category

```
DELETE /api/v1/menu/categories/{vendorId}/{categoryId}
```

**Requires:** VENDOR role

#### Toggle Category Status

```
PATCH /api/v1/menu/categories/{vendorId}/{categoryId}/toggle
```

**Requires:** VENDOR role

### 3. Menu Items

#### Create Menu Item

```
POST /api/v1/menu/items/{vendorId}
```

**Requires:** VENDOR role

**Request Body:**

```json
{
  "categoryId": 1,
  "name": "Chicken Wings",
  "description": "Crispy chicken wings with your choice of sauce",
  "price": 12.99,
  "originalPrice": 15.99,
  "isAvailable": true,
  "isFeatured": false,
  "isVegetarian": false,
  "isVegan": false,
  "isGlutenFree": false,
  "isSpicy": true,
  "spiceLevel": 2,
  "preparationTimeMinutes": 15,
  "calories": 450,
  "proteinGrams": 25.5,
  "carbsGrams": 15.2,
  "fatGrams": 28.8,
  "fiberGrams": 2.1,
  "sodiumMg": 850,
  "sugarGrams": 3.2,
  "allergens": "Contains: Dairy, Gluten",
  "ingredients": "Chicken wings, flour, spices, sauce",
  "cookingInstructions": "Deep fry until golden brown",
  "displayOrder": 1
}
```

#### Update Menu Item

```
PUT /api/v1/menu/items/{vendorId}/{itemId}
```

**Requires:** VENDOR role

#### Get Menu Item

```
GET /api/v1/menu/items/{vendorId}/{itemId}
```

#### Get All Menu Items

```
GET /api/v1/menu/items/{vendorId}?availableOnly=false
```

#### Get Menu Items by Category

```
GET /api/v1/menu/items/{vendorId}/category/{categoryId}
```

#### Get Featured Menu Items

```
GET /api/v1/menu/items/{vendorId}/featured
```

#### Delete Menu Item

```
DELETE /api/v1/menu/items/{vendorId}/{itemId}
```

**Requires:** VENDOR role

#### Toggle Item Availability

```
PATCH /api/v1/menu/items/{vendorId}/{itemId}/toggle
```

**Requires:** VENDOR role

### 4. Search and Filter

#### Search Menu Items

```
GET /api/v1/menu/items/{vendorId}/search?searchTerm=chicken&page=0&size=20&sortBy=displayOrder&sortDir=ASC
```

#### Filter Menu Items

```
GET /api/v1/menu/items/{vendorId}/filter?categoryId=1&isVegetarian=true&isSpicy=false&minPrice=10&maxPrice=20&page=0&size=20
```

**Filter Parameters:**

- `categoryId` - Filter by category
- `isVegetarian` - Filter vegetarian items
- `isVegan` - Filter vegan items
- `isGlutenFree` - Filter gluten-free items
- `isSpicy` - Filter spicy items
- `minPrice` - Minimum price filter
- `maxPrice` - Maximum price filter
- `searchTerm` - Text search in name and description

### 5. Dietary Preferences

#### Get Vegetarian Items

```
GET /api/v1/menu/items/{vendorId}/vegetarian
```

#### Get Vegan Items

```
GET /api/v1/menu/items/{vendorId}/vegan
```

#### Get Gluten-Free Items

```
GET /api/v1/menu/items/{vendorId}/gluten-free
```

## Data Models

### MenuCategoryDto

```json
{
  "id": 1,
  "vendorId": 1,
  "name": "Appetizers",
  "description": "Start your meal with our delicious appetizers",
  "displayOrder": 1,
  "isActive": true,
  "isFeatured": false,
  "imageUrl": "https://example.com/appetizers.jpg",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "itemCount": 5
}
```

### MenuItemDto

```json
{
  "id": 1,
  "vendorId": 1,
  "categoryId": 1,
  "categoryName": "Appetizers",
  "name": "Chicken Wings",
  "description": "Crispy chicken wings with your choice of sauce",
  "price": 12.99,
  "originalPrice": 15.99,
  "isAvailable": true,
  "isFeatured": false,
  "isVegetarian": false,
  "isVegan": false,
  "isGlutenFree": false,
  "isSpicy": true,
  "spiceLevel": 2,
  "preparationTimeMinutes": 15,
  "calories": 450,
  "proteinGrams": 25.5,
  "carbsGrams": 15.2,
  "fatGrams": 28.8,
  "fiberGrams": 2.1,
  "sodiumMg": 850,
  "sugarGrams": 3.2,
  "allergens": "Contains: Dairy, Gluten",
  "ingredients": "Chicken wings, flour, spices, sauce",
  "cookingInstructions": "Deep fry until golden brown",
  "displayOrder": 1,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00",
  "primaryImageUrl": "https://example.com/wings.jpg",
  "hasDiscount": true,
  "discountPercentage": 18.76
}
```

### MenuItemImageDto

```json
{
  "id": 1,
  "menuItemId": 1,
  "imageUrl": "https://example.com/wings.jpg",
  "imageType": "JPEG",
  "fileName": "chicken_wings.jpg",
  "fileSizeBytes": 1024000,
  "widthPixels": 1920,
  "heightPixels": 1080,
  "isPrimary": true,
  "displayOrder": 1,
  "altText": "Crispy chicken wings",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### MenuItemAvailabilityDto

```json
{
  "id": 1,
  "menuItemId": 1,
  "dayOfWeek": "MONDAY",
  "isAvailable": true,
  "availableFrom": "11:00:00",
  "availableUntil": "22:00:00",
  "maxQuantityPerDay": 50,
  "currentQuantityAvailable": 45,
  "isSpecialOffer": false,
  "specialPrice": null,
  "specialOfferStartDate": null,
  "specialOfferEndDate": null,
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

## Error Handling

All API responses follow a consistent format:

**Success Response:**

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {...}
}
```

**Error Response:**

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

**Common HTTP Status Codes:**

- `200 OK` - Success
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Validation Rules

### Menu Category Validation

- `name`: Required, max 255 characters
- `description`: Optional, max 1000 characters
- `displayOrder`: Required, must be >= 0
- `imageUrl`: Optional, max 500 characters

### Menu Item Validation

- `categoryId`: Required
- `name`: Required, max 255 characters
- `description`: Optional, max 2000 characters
- `price`: Required, must be > 0
- `originalPrice`: Optional, must be > 0
- `spiceLevel`: Optional, must be >= 0
- `preparationTimeMinutes`: Optional, must be >= 1
- `calories`: Optional, must be >= 0
- `proteinGrams`, `carbsGrams`, `fatGrams`, `fiberGrams`, `sugarGrams`: Optional, must be >= 0
- `sodiumMg`: Optional, must be >= 0
- `allergens`: Optional, max 500 characters
- `ingredients`: Optional, max 2000 characters
- `cookingInstructions`: Optional, max 2000 characters
- `displayOrder`: Optional, must be >= 0

## Security

- All write operations (POST, PUT, DELETE, PATCH) require the `VENDOR` role
- Read operations are generally public but may be restricted based on business rules
- Input validation is performed on all endpoints
- SQL injection protection is provided by JPA/Hibernate
- XSS protection is provided by Spring Security

## Performance Considerations

- Pagination is supported for large result sets
- Database indexes are created on frequently queried fields
- Lazy loading is used for related entities
- Caching can be implemented for frequently accessed data

## Testing

Comprehensive unit tests are provided for all endpoints in `MenuControllerTest.java`. The tests cover:

- Successful operations
- Error scenarios
- Validation failures
- Authentication requirements
- Authorization checks

## Database Schema

### ck_menu_categories

```sql
CREATE TABLE ck_menu_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    vendor_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES ck_vendor_profiles(id)
);
```

### ck_menu_items

```sql
CREATE TABLE ck_menu_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    vendor_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    is_vegetarian BOOLEAN NOT NULL DEFAULT FALSE,
    is_vegan BOOLEAN NOT NULL DEFAULT FALSE,
    is_gluten_free BOOLEAN NOT NULL DEFAULT FALSE,
    is_spicy BOOLEAN NOT NULL DEFAULT FALSE,
    spice_level INT DEFAULT 0,
    preparation_time_minutes INT,
    calories INT,
    protein_grams DECIMAL(5,2),
    carbs_grams DECIMAL(5,2),
    fat_grams DECIMAL(5,2),
    fiber_grams DECIMAL(5,2),
    sodium_mg INT,
    sugar_grams DECIMAL(5,2),
    allergens VARCHAR(500),
    ingredients VARCHAR(2000),
    cooking_instructions VARCHAR(2000),
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES ck_vendor_profiles(id),
    FOREIGN KEY (category_id) REFERENCES ck_menu_categories(id)
);
```

### ck_menu_item_images

```sql
CREATE TABLE ck_menu_item_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_item_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_type VARCHAR(50),
    file_name VARCHAR(255),
    file_size_bytes BIGINT,
    width_pixels INT,
    height_pixels INT,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INT NOT NULL DEFAULT 0,
    alt_text VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_item_id) REFERENCES ck_menu_items(id)
);
```

### ck_menu_item_availability

```sql
CREATE TABLE ck_menu_item_availability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    menu_item_id BIGINT NOT NULL,
    day_of_week VARCHAR(10) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    available_from TIME,
    available_until TIME,
    max_quantity_per_day INT,
    current_quantity_available INT,
    is_special_offer BOOLEAN NOT NULL DEFAULT FALSE,
    special_price DECIMAL(10,2),
    special_offer_start_date TIMESTAMP,
    special_offer_end_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (menu_item_id) REFERENCES ck_menu_items(id)
);
```

## Usage Examples

### Creating a Complete Menu

1. **Create Categories:**

```bash
curl -X POST "http://localhost:8080/api/v1/menu/categories/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Appetizers",
    "description": "Start your meal with our delicious appetizers",
    "displayOrder": 1,
    "isActive": true,
    "isFeatured": false
  }'
```

2. **Create Menu Items:**

```bash
curl -X POST "http://localhost:8080/api/v1/menu/items/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "categoryId": 1,
    "name": "Chicken Wings",
    "description": "Crispy chicken wings with your choice of sauce",
    "price": 12.99,
    "isAvailable": true,
    "isSpicy": true,
    "spiceLevel": 2,
    "calories": 450
  }'
```

3. **Get Menu Overview:**

```bash
curl -X GET "http://localhost:8080/api/v1/menu/overview/1"
```

4. **Search for Items:**

```bash
curl -X GET "http://localhost:8080/api/v1/menu/items/1/search?searchTerm=chicken&page=0&size=10"
```

5. **Filter Vegetarian Items:**

```bash
curl -X GET "http://localhost:8080/api/v1/menu/items/1/vegetarian"
```

This comprehensive menu management system provides all the functionality needed for a restaurant to manage their menu effectively, with proper security, validation, and performance considerations.
