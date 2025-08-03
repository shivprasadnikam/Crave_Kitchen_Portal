# Menu Management System - Logging Guide

## 📋 Overview

This guide explains the comprehensive logging system implemented for the Crave Kitchen Portal Menu Management System. The logging system provides detailed tracking of all menu operations, performance metrics, and error handling.

## 🏗️ Logging Architecture

### 1. Logging Framework

- **Framework**: SLF4J with Logback
- **Pattern**: Structured logging with timestamps, thread info, and log levels
- **Output**: Console and file logging with rotation

### 2. Log Levels Used

- **DEBUG**: Detailed operation tracking and SQL queries
- **INFO**: Successful operations and business events
- **WARN**: Non-critical issues and business rule violations
- **ERROR**: Exceptions and critical failures

## 📊 Logging Configuration

### Application Properties Configuration

```properties
# Root logging level
logging.level.root=INFO

# Application-specific logging
logging.level.com.example.crave.kitchen.portal=DEBUG
logging.level.com.example.crave.kitchen.portal.service=DEBUG
logging.level.com.example.crave.kitchen.portal.impl=DEBUG
logging.level.com.example.crave.kitchen.portal.controller=DEBUG
logging.level.com.example.crave.kitchen.portal.repository=DEBUG

# Menu-specific logging
logging.level.com.example.crave.kitchen.portal.service.MenuService=DEBUG
logging.level.com.example.crave.kitchen.portal.impl.MenuServiceImpl=DEBUG
logging.level.com.example.crave.kitchen.portal.controller.MenuController=DEBUG

# SQL logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# File logging
logging.file.name=logs/crave-kitchen-portal.log
logging.file.max-size=10MB
logging.file.max-history=30
```

## 🔍 Logging Categories

### 1. Menu Categories Operations

#### Create Category

```log
INFO  - POST /api/menu/categories - Creating category: Appetizers
INFO  - Successfully created menu category with ID: 1 and name: Appetizers
```

#### Update Category

```log
INFO  - PUT /api/menu/categories/1 - Updating category: Updated Appetizers
INFO  - Successfully updated menu category with ID: 1 and name: Updated Appetizers
```

#### Delete Category

```log
INFO  - DELETE /api/menu/categories/1
WARN  - Cannot delete category with ID: 1 - it has 5 associated menu items
INFO  - Successfully deleted menu category with ID: 1
```

### 2. Menu Items Operations

#### Create Menu Item

```log
INFO  - POST /api/menu/items - Creating menu item: Spring Rolls
INFO  - Successfully created menu item with ID: 1 and name: Spring Rolls
```

#### Search Menu Items

```log
INFO  - GET /api/menu/items/search - vendorId: 1, searchTerm: 'chicken', page: 0, size: 20
INFO  - Search completed successfully. Found 3 items for search term: 'chicken'
```

#### Filter Menu Items

```log
INFO  - GET /api/menu/items - vendorId: 1, categoryId: 1, isAvailable: true, isFeatured: false, isVegetarian: true, isVegan: false, isGlutenFree: false, isSpicy: false, minPrice: 5.00, maxPrice: 15.00, searchTerm: null, page: 0, size: 20, sortBy: displayOrder, sortDir: asc
INFO  - Successfully fetched 5 menu items for vendorId: 1
```

### 3. Image Management Operations

#### Upload Image

```log
INFO  - POST /api/menu/items/1/images - Uploading image: spring-rolls.jpg, size: 245760 bytes, isPrimary: true
INFO  - Successfully uploaded image with ID: 1 for menu item ID: 1
```

#### Set Primary Image

```log
INFO  - PUT /api/menu/items/1/images/1/primary
INFO  - Successfully set primary image for menu item ID: 1 with image ID: 1
```

### 4. Availability Management

#### Update Availability

```log
INFO  - PUT /api/menu/items/1/availability - Updating 7 availability records
INFO  - Successfully updated availability for menu item ID: 1 with 7 records
```

#### Create Special Offer

```log
INFO  - POST /api/menu/items/1/special-offers - Creating special offer for day: 5
INFO  - Successfully created special offer with ID: 1 for menu item ID: 1
```

### 5. Menu Overview & Analytics

#### Generate Overview

```log
INFO  - GET /api/menu/overview - vendorId: 1
INFO  - Successfully generated menu overview for vendor ID: 1 - Categories: 4, Items: 25
```

#### Get Featured Items

```log
INFO  - GET /api/menu/items/featured - vendorId: 1, limit: 10
INFO  - Successfully retrieved 8 featured items for vendor ID: 1
```

## 🚨 Error Logging

### 1. Validation Errors

```log
ERROR - Error creating menu item: Spring Rolls
java.lang.IllegalArgumentException: Category not found with ID: 999
```

### 2. Database Errors

```log
ERROR - Error fetching menu categories for vendorId: 1
org.springframework.dao.DataAccessException: Connection timeout
```

### 3. Security Errors

```log
WARN  - Unauthorized access attempt to menu categories for vendorId: 1
```

## 📈 Performance Logging

### 1. Slow Query Detection

```log
WARN  - Slow query detected: MenuItemRepository.findByVendorIdAndFilters took 1250ms
```

### 2. Database Connection Pool

```log
DEBUG - HikariCP pool status: active=5, idle=10, total=15
```

### 3. Cache Performance

```log
DEBUG - Cache hit for menu category with ID: 1
DEBUG - Cache miss for menu category with ID: 2
```

## 🔧 Logging Implementation Details

### 1. Service Layer Logging (MenuServiceImpl)

```java
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {

    @Override
    public MenuCategoryDto createCategory(CreateMenuCategoryRequestDto requestDto) {
        log.info("Creating new menu category: {}", requestDto.getName());

        try {
            // Business logic
            MenuCategoryEntity savedCategory = menuCategoryRepository.save(category);
            log.info("Successfully created menu category with ID: {} and name: {}",
                    savedCategory.getId(), savedCategory.getName());

            return convertToCategoryDto(savedCategory);
        } catch (Exception e) {
            log.error("Error creating menu category: {}", requestDto.getName(), e);
            throw e;
        }
    }
}
```

### 2. Controller Layer Logging (MenuController)

```java
@Slf4j
@RestController
public class MenuController {

    @PostMapping("/categories")
    public ResponseEntity<ApiResponseDto<MenuCategoryDto>> createCategory(
            @Valid @RequestBody CreateMenuCategoryRequestDto requestDto) {

        log.info("POST /api/menu/categories - Creating category: {}", requestDto.getName());

        try {
            MenuCategoryDto createdCategory = menuService.createCategory(requestDto);
            log.info("Successfully created category with ID: {} and name: {}",
                    createdCategory.getId(), createdCategory.getName());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponseDto.<MenuCategoryDto>builder()
                            .success(true)
                            .message("Category created successfully")
                            .data(createdCategory)
                            .build());
        } catch (Exception e) {
            log.error("Error creating category: {}", requestDto.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.<MenuCategoryDto>builder()
                            .success(false)
                            .message("Error creating category: " + e.getMessage())
                            .build());
        }
    }
}
```

## 📁 Log File Management

### 1. Log File Structure

```
logs/
├── crave-kitchen-portal.log          # Current log file
├── crave-kitchen-portal.log.1        # Previous log file
├── crave-kitchen-portal.log.2        # Older log file
└── ...
```

### 2. Log Rotation Settings

- **Max File Size**: 10MB
- **Max History**: 30 files
- **Total Size Cap**: 1GB
- **Compression**: Automatic for archived files

### 3. Log File Content Example

```log
2024-01-15 10:30:15.123 [http-nio-8080-exec-1] INFO  c.e.c.k.p.c.MenuController - POST /api/menu/categories - Creating category: Appetizers
2024-01-15 10:30:15.145 [http-nio-8080-exec-1] INFO  c.e.c.k.p.i.MenuServiceImpl - Creating new menu category: Appetizers
2024-01-15 10:30:15.167 [http-nio-8080-exec-1] DEBUG o.h.SQL - insert into ck_menu_categories (created_at, description, display_order, image_url, is_active, is_featured, name, updated_at, vendor_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
2024-01-15 10:30:15.168 [http-nio-8080-exec-1] TRACE o.h.t.d.sql.BasicBinder - binding parameter [1] as [TIMESTAMP] - [2024-01-15T10:30:15.167]
2024-01-15 10:30:15.169 [http-nio-8080-exec-1] TRACE o.h.t.d.sql.BasicBinder - binding parameter [2] as [VARCHAR] - [Delicious starters to begin your meal]
2024-01-15 10:30:15.234 [http-nio-8080-exec-1] INFO  c.e.c.k.p.i.MenuServiceImpl - Successfully created menu category with ID: 1 and name: Appetizers
2024-01-15 10:30:15.245 [http-nio-8080-exec-1] INFO  c.e.c.k.p.c.MenuController - Successfully created category with ID: 1 and name: Appetizers
```

## 🎯 Logging Best Practices

### 1. Structured Logging

- Use consistent log message formats
- Include relevant context (IDs, names, counts)
- Separate success and error scenarios

### 2. Performance Considerations

- Use appropriate log levels
- Avoid logging sensitive data
- Use parameterized logging for performance

### 3. Error Handling

- Always log exceptions with stack traces
- Include context information in error logs
- Use specific error messages

### 4. Security

- Never log passwords or tokens
- Be careful with user input in logs
- Consider log file permissions

## 🔍 Log Analysis Tools

### 1. Built-in Analysis

```bash
# Count log entries by level
grep -c "ERROR" logs/crave-kitchen-portal.log
grep -c "WARN" logs/crave-kitchen-portal.log
grep -c "INFO" logs/crave-kitchen-portal.log

# Find slow operations
grep "took [0-9]\{4,\}ms" logs/crave-kitchen-portal.log

# Find errors by operation
grep "Error creating menu item" logs/crave-kitchen-portal.log
```

### 2. External Tools

- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Splunk**
- **Grafana + Loki**
- **AWS CloudWatch**

## 📊 Monitoring and Alerting

### 1. Key Metrics to Monitor

- Error rate by operation type
- Response times for menu operations
- Database connection pool usage
- Cache hit/miss ratios

### 2. Alert Thresholds

- Error rate > 5%
- Response time > 2 seconds
- Database connection pool > 80% utilization

### 3. Health Checks

```bash
# Check log file size
ls -lh logs/crave-kitchen-portal.log

# Check recent errors
tail -100 logs/crave-kitchen-portal.log | grep ERROR

# Check application health
curl http://localhost:8080/actuator/health
```

## 🚀 Production Considerations

### 1. Log Aggregation

- Centralize logs from multiple instances
- Use structured logging formats (JSON)
- Implement log shipping to external systems

### 2. Performance Optimization

- Use async logging for high-volume operations
- Implement log sampling for DEBUG level
- Configure appropriate buffer sizes

### 3. Security

- Encrypt log files at rest
- Implement log file access controls
- Regular log file rotation and cleanup

### 4. Compliance

- Ensure logs meet audit requirements
- Implement log retention policies
- Document logging procedures

## 📝 Logging Checklist

### Development

- [ ] All operations have appropriate log entries
- [ ] Error scenarios are properly logged
- [ ] Performance metrics are tracked
- [ ] Log levels are appropriately set

### Testing

- [ ] Log files are generated correctly
- [ ] Log rotation works as expected
- [ ] Error logging captures necessary details
- [ ] Performance logging provides useful metrics

### Production

- [ ] Log aggregation is configured
- [ ] Monitoring and alerting are set up
- [ ] Log retention policies are implemented
- [ ] Security measures are in place

## 🔗 Related Documentation

- [Menu API Documentation](MENU_API_DOCUMENTATION.md)
- [cURL Examples](curl_examples.md)
- [Database Schema](src/sql/oracle_menu_tables.sql)
- [Application Properties](src/main/resources/application.properties)

---

**Note**: This logging system provides comprehensive visibility into the menu management operations, enabling effective monitoring, debugging, and performance optimization of the Crave Kitchen Portal.
