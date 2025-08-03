# cURL Examples for Crave Kitchen Portal Menu APIs

## 🔐 Authentication

First, you need to authenticate and get a JWT token:

```bash
# Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "vendor@example.com",
    "password": "password123"
  }'

# Use the returned token in subsequent requests
export JWT_TOKEN="your_jwt_token_here"
```

## 📋 Menu Categories

### Get All Categories

```bash
curl -X GET "http://localhost:8080/api/menu/categories?vendorId=1&isActive=true" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Create Category

```bash
curl -X POST http://localhost:8080/api/menu/categories \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Main Course",
    "description": "Delicious main course dishes",
    "displayOrder": 1,
    "isActive": true,
    "isFeatured": false,
    "imageUrl": "https://example.com/images/main-course.jpg"
  }'
```

## 🍽️ Menu Items

### Get All Menu Items

```bash
curl -X GET "http://localhost:8080/api/menu/items?vendorId=1&isAvailable=true" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Create Menu Item (IMPORTANT: Include categoryId)

```bash
curl -X POST http://localhost:8080/api/menu/items \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "name": "Grilled Chicken Breast",
    "description": "Juicy grilled chicken breast with herbs and spices",
    "price": 15.99,
    "originalPrice": 18.99,
    "isAvailable": true,
    "isFeatured": true,
    "isVegetarian": false,
    "isVegan": false,
    "isGlutenFree": true,
    "isSpicy": false,
    "spiceLevel": 1,
    "preparationTimeMinutes": 25,
    "calories": 350,
    "proteinGrams": 45.5,
    "carbsGrams": 2.0,
    "fatGrams": 8.0,
    "fiberGrams": 1.5,
    "sodiumMg": 450,
    "sugarGrams": 1.0,
    "allergens": "None",
    "ingredients": "Chicken breast, olive oil, herbs, salt, pepper",
    "cookingInstructions": "Marinate chicken, grill for 8-10 minutes each side",
    "displayOrder": 1
  }'
```

### Create Vegetarian Menu Item

```bash
curl -X POST http://localhost:8080/api/menu/items \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 2,
    "name": "Vegetable Pasta",
    "description": "Fresh vegetables with al dente pasta",
    "price": 12.99,
    "isAvailable": true,
    "isFeatured": false,
    "isVegetarian": true,
    "isVegan": true,
    "isGlutenFree": false,
    "isSpicy": false,
    "spiceLevel": 0,
    "preparationTimeMinutes": 20,
    "calories": 280,
    "proteinGrams": 8.0,
    "carbsGrams": 45.0,
    "fatGrams": 4.0,
    "fiberGrams": 6.0,
    "sodiumMg": 320,
    "sugarGrams": 3.0,
    "allergens": "Gluten",
    "ingredients": "Pasta, broccoli, carrots, bell peppers, olive oil",
    "cookingInstructions": "Boil pasta, sauté vegetables, combine",
    "displayOrder": 2
  }'
```

### Create Spicy Menu Item

```bash
curl -X POST http://localhost:8080/api/menu/items \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "name": "Spicy Chicken Curry",
    "description": "Hot and spicy chicken curry with rice",
    "price": 16.99,
    "isAvailable": true,
    "isFeatured": true,
    "isVegetarian": false,
    "isVegan": false,
    "isGlutenFree": true,
    "isSpicy": true,
    "spiceLevel": 4,
    "preparationTimeMinutes": 35,
    "calories": 420,
    "proteinGrams": 38.0,
    "carbsGrams": 25.0,
    "fatGrams": 12.0,
    "fiberGrams": 4.0,
    "sodiumMg": 680,
    "sugarGrams": 2.0,
    "allergens": "None",
    "ingredients": "Chicken, curry spices, coconut milk, vegetables",
    "cookingInstructions": "Sauté chicken, add spices, simmer with coconut milk",
    "displayOrder": 3
  }'
```

## 🖼️ Menu Item Images

### Upload Image

```bash
curl -X POST http://localhost:8080/api/menu/items/1/images \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -F "file=@/path/to/image.jpg" \
  -F "imageType=MAIN" \
  -F "isPrimary=true" \
  -F "displayOrder=1" \
  -F "altText=Grilled Chicken Breast"
```

### Set Primary Image

```bash
curl -X PUT http://localhost:8080/api/menu/items/1/images/1/primary \
  -H "Authorization: Bearer $JWT_TOKEN"
```

## ⏰ Menu Item Availability

### Set Availability

```bash
curl -X PUT http://localhost:8080/api/menu/items/1/availability \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '[
    {
      "dayOfWeek": 1,
      "isAvailable": true,
      "availableFrom": "11:00:00",
      "availableUntil": "22:00:00",
      "maxQuantityPerDay": 50,
      "currentQuantityAvailable": 50
    },
    {
      "dayOfWeek": 2,
      "isAvailable": true,
      "availableFrom": "11:00:00",
      "availableUntil": "22:00:00",
      "maxQuantityPerDay": 50,
      "currentQuantityAvailable": 50
    }
  ]'
```

### Create Special Offer

```bash
curl -X POST http://localhost:8080/api/menu/items/1/special-offers \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dayOfWeek": 1,
    "isAvailable": true,
    "availableFrom": "11:00:00",
    "availableUntil": "22:00:00",
    "maxQuantityPerDay": 20,
    "currentQuantityAvailable": 20,
    "isSpecialOffer": true,
    "specialOfferPrice": 12.99,
    "specialOfferDescription": "Monday Special - 20% off!",
    "specialOfferValidFrom": "2024-01-01T00:00:00",
    "specialOfferValidUntil": "2024-12-31T23:59:59"
  }'
```

## 📊 Menu Overview

### Get Menu Overview

```bash
curl -X GET "http://localhost:8080/api/menu/overview?vendorId=1" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Get Featured Items

```bash
curl -X GET "http://localhost:8080/api/menu/items/featured?vendorId=1" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Get Items by Dietary Preference

```bash
# Vegetarian items
curl -X GET "http://localhost:8080/api/menu/items/dietary/VEGETARIAN?vendorId=1" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"

# Vegan items
curl -X GET "http://localhost:8080/api/menu/items/dietary/VEGAN?vendorId=1" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"

# Gluten-free items
curl -X GET "http://localhost:8080/api/menu/items/dietary/GLUTEN_FREE?vendorId=1" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"

# Spicy items
curl -X GET "http://localhost:8080/api/menu/items/dietary/SPICY?vendorId=1" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"
```

## 🔍 Search Menu Items

### Search by Name/Description

```bash
curl -X GET "http://localhost:8080/api/menu/items/search?vendorId=1&searchTerm=chicken" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"
```

## 🗑️ Delete Operations

### Delete Menu Item

```bash
curl -X DELETE http://localhost:8080/api/menu/items/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### Delete Category

```bash
curl -X DELETE http://localhost:8080/api/menu/categories/1 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

## 📝 Complete Menu Setup Example

### Step 1: Create Categories

```bash
# Create Main Course category
curl -X POST http://localhost:8080/api/menu/categories \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Main Course",
    "description": "Delicious main course dishes",
    "displayOrder": 1,
    "isActive": true,
    "isFeatured": true
  }'

# Create Appetizers category
curl -X POST http://localhost:8080/api/menu/categories \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Appetizers",
    "description": "Light starters and appetizers",
    "displayOrder": 2,
    "isActive": true,
    "isFeatured": false
  }'

# Create Desserts category
curl -X POST http://localhost:8080/api/menu/categories \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Desserts",
    "description": "Sweet treats and desserts",
    "displayOrder": 3,
    "isActive": true,
    "isFeatured": false
  }'
```

### Step 2: Create Menu Items

```bash
# Create a main course item
curl -X POST http://localhost:8080/api/menu/items \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 1,
    "name": "Grilled Salmon",
    "description": "Fresh Atlantic salmon grilled to perfection",
    "price": 24.99,
    "isAvailable": true,
    "isFeatured": true,
    "isVegetarian": false,
    "isVegan": false,
    "isGlutenFree": true,
    "isSpicy": false,
    "preparationTimeMinutes": 30,
    "calories": 380,
    "proteinGrams": 42.0,
    "carbsGrams": 0.0,
    "fatGrams": 22.0,
    "displayOrder": 1
  }'

# Create an appetizer
curl -X POST http://localhost:8080/api/menu/items \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 2,
    "name": "Bruschetta",
    "description": "Toasted bread with fresh tomatoes and basil",
    "price": 8.99,
    "isAvailable": true,
    "isFeatured": false,
    "isVegetarian": true,
    "isVegan": true,
    "isGlutenFree": false,
    "isSpicy": false,
    "preparationTimeMinutes": 10,
    "calories": 120,
    "proteinGrams": 3.0,
    "carbsGrams": 18.0,
    "fatGrams": 4.0,
    "displayOrder": 1
  }'

# Create a dessert
curl -X POST http://localhost:8080/api/menu/items \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 3,
    "name": "Chocolate Lava Cake",
    "description": "Warm chocolate cake with molten center",
    "price": 9.99,
    "isAvailable": true,
    "isFeatured": true,
    "isVegetarian": true,
    "isVegan": false,
    "isGlutenFree": false,
    "isSpicy": false,
    "preparationTimeMinutes": 15,
    "calories": 320,
    "proteinGrams": 6.0,
    "carbsGrams": 35.0,
    "fatGrams": 18.0,
    "displayOrder": 1
  }'
```

## ⚠️ Important Notes

1. **Category ID is Required**: Every menu item creation request MUST include a `categoryId` field
2. **Authentication**: All requests require a valid JWT token in the Authorization header
3. **Vendor ID**: Currently hardcoded to 1 in the backend (will be extracted from JWT token later)
4. **Validation**: All requests are validated for required fields and data types
5. **Error Handling**: Check the response for success/error messages

## 🔧 Troubleshooting

### Common Issues:

1. **Missing categoryId**: Returns validation error
2. **Invalid JWT token**: Returns 401 Unauthorized
3. **Invalid categoryId**: Returns error if category doesn't exist
4. **Missing required fields**: Returns validation errors

### Test Connection:

```bash
# Test if server is running
curl -X GET http://localhost:8080/actuator/health

# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "vendor@example.com", "password": "password123"}'
```
