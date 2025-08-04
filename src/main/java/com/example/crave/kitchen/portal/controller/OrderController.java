package com.example.crave.kitchen.portal.controller;

import com.example.crave.kitchen.portal.dto.*;
import com.example.crave.kitchen.portal.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@PreAuthorize("hasRole('VENDOR')")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    /**
     * GET /api/orders - Get all orders with filters
     * 
     * @param filterRequest Filter parameters for orders
     * @return Paginated list of orders
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> getAllOrders(
            @ModelAttribute OrderFilterRequestDto filterRequest) {

        log.info("Getting all orders with filters: {}", filterRequest);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            Page<OrderDto> orders = orderService.getAllOrders(filterRequest, vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Orders retrieved successfully", orders));

        } catch (Exception e) {
            log.error("Error getting orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve orders", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/{id} - Get order by ID
     * 
     * @param id Order ID
     * @return Order details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OrderDto>> getOrderById(@PathVariable Long id) {

        log.info("Getting order by ID: {}", id);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            OrderDto order = orderService.getOrderById(id, vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order retrieved successfully", order));

        } catch (RuntimeException e) {
            log.error("Error getting order by ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("Order not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting order by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve order", e.getMessage()));
        }
    }

    /**
     * PUT /api/orders/{id} - Update order status
     * 
     * @param id      Order ID
     * @param request Update order status request
     * @return Updated order details
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDto request) {

        log.info("Updating order status for order ID: {} with request: {}", id, request);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            OrderDto updatedOrder = orderService.updateOrderStatus(id, request, vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order status updated successfully", updatedOrder));

        } catch (RuntimeException e) {
            log.error("Error updating order status for ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("Order not found or access denied", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating order status for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to update order status", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/history - Get order history
     * 
     * @param filterRequest Filter parameters for order history
     * @return List of historical orders
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrderHistory(
            @ModelAttribute OrderFilterRequestDto filterRequest) {

        log.info("Getting order history with filters: {}", filterRequest);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            List<OrderDto> orderHistory = orderService.getOrderHistory(vendorId, filterRequest);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order history retrieved successfully", orderHistory));

        } catch (Exception e) {
            log.error("Error getting order history: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve order history", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/number/{orderNumber} - Get order by order number
     * 
     * @param orderNumber Order number
     * @return Order details
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponseDto<OrderDto>> getOrderByOrderNumber(
            @PathVariable String orderNumber) {

        log.info("Getting order by order number: {}", orderNumber);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            OrderDto order = orderService.getOrderByOrderNumber(orderNumber, vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order retrieved successfully", order));

        } catch (RuntimeException e) {
            log.error("Error getting order by number {}: {}", orderNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("Order not found", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting order by number {}: {}", orderNumber, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve order", e.getMessage()));
        }
    }

    // Enhanced endpoints for specific order statuses
    /**
     * GET /api/orders/pending - Get pending orders only
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> getPendingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Getting pending orders with page: {}, size: {}", page, size);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDto> orders = orderService.getPendingOrders(vendorId, pageable);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Pending orders retrieved successfully", orders));

        } catch (Exception e) {
            log.error("Error getting pending orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve pending orders", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/preparing - Get orders being prepared
     */
    @GetMapping("/preparing")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> getPreparingOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Getting preparing orders with page: {}, size: {}", page, size);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDto> orders = orderService.getPreparingOrders(vendorId, pageable);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Preparing orders retrieved successfully", orders));

        } catch (Exception e) {
            log.error("Error getting preparing orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve preparing orders", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/ready - Get ready orders
     */
    @GetMapping("/ready")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> getReadyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Getting ready orders with page: {}, size: {}", page, size);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDto> orders = orderService.getReadyOrders(vendorId, pageable);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Ready orders retrieved successfully", orders));

        } catch (Exception e) {
            log.error("Error getting ready orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve ready orders", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/completed - Get completed orders
     */
    @GetMapping("/completed")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> getCompletedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Getting completed orders with page: {}, size: {}", page, size);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDto> orders = orderService.getCompletedOrders(vendorId, pageable);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Completed orders retrieved successfully", orders));

        } catch (Exception e) {
            log.error("Error getting completed orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve completed orders", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/cancelled - Get cancelled orders
     */
    @GetMapping("/cancelled")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> getCancelledOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Getting cancelled orders with page: {}, size: {}", page, size);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDto> orders = orderService.getCancelledOrders(vendorId, pageable);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Cancelled orders retrieved successfully", orders));

        } catch (Exception e) {
            log.error("Error getting cancelled orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to retrieve cancelled orders", e.getMessage()));
        }
    }

    // Order action endpoints
    /**
     * POST /api/orders/{id}/accept - Accept order
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponseDto<OrderDto>> acceptOrder(
            @PathVariable Long id,
            @Valid @RequestBody AcceptOrderRequestDto request) {

        log.info("Accepting order ID: {} with request: {}", id, request);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            OrderDto updatedOrder = orderService.acceptOrder(id, request.getNotes(), vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order accepted successfully", updatedOrder));

        } catch (RuntimeException e) {
            log.error("Error accepting order ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Failed to accept order", e.getMessage()));
        } catch (Exception e) {
            log.error("Error accepting order ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to accept order", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/reject - Reject order
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponseDto<OrderDto>> rejectOrder(
            @PathVariable Long id,
            @Valid @RequestBody RejectOrderRequestDto request) {

        log.info("Rejecting order ID: {} with request: {}", id, request);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            OrderDto updatedOrder = orderService.rejectOrder(id, request.getReason(), vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order rejected successfully", updatedOrder));

        } catch (RuntimeException e) {
            log.error("Error rejecting order ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Failed to reject order", e.getMessage()));
        } catch (Exception e) {
            log.error("Error rejecting order ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to reject order", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/start-preparing - Start preparing order
     */
    @PostMapping("/{id}/start-preparing")
    public ResponseEntity<ApiResponseDto<OrderDto>> startPreparingOrder(
            @PathVariable Long id,
            @Valid @RequestBody StartPreparingRequestDto request) {

        log.info("Starting preparation for order ID: {} with request: {}", id, request);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            OrderDto updatedOrder = orderService.startPreparingOrder(
                    id, request.getEstimatedPreparationTime(), request.getNotes(), vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order preparation started successfully", updatedOrder));

        } catch (RuntimeException e) {
            log.error("Error starting preparation for order ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Failed to start order preparation", e.getMessage()));
        } catch (Exception e) {
            log.error("Error starting preparation for order ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to start order preparation", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/ready - Mark order as ready
     */
    @PostMapping("/{id}/ready")
    public ResponseEntity<ApiResponseDto<OrderDto>> markOrderReady(
            @PathVariable Long id,
            @Valid @RequestBody MarkReadyRequestDto request) {

        log.info("Marking order as ready ID: {} with request: {}", id, request);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            OrderDto updatedOrder = orderService.markOrderReady(
                    id, request.getActualPreparationTime(), request.getNotes(), vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order marked as ready successfully", updatedOrder));

        } catch (RuntimeException e) {
            log.error("Error marking order as ready ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Failed to mark order as ready", e.getMessage()));
        } catch (Exception e) {
            log.error("Error marking order as ready ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to mark order as ready", e.getMessage()));
        }
    }

    /**
     * POST /api/orders/{id}/complete - Mark order as completed
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponseDto<OrderDto>> completeOrder(
            @PathVariable Long id,
            @Valid @RequestBody CompleteOrderRequestDto request) {

        log.info("Completing order ID: {} with request: {}", id, request);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            OrderDto updatedOrder = orderService.completeOrder(id, request.getNotes(), vendorId);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Order completed successfully", updatedOrder));

        } catch (RuntimeException e) {
            log.error("Error completing order ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponseDto.error("Failed to complete order", e.getMessage()));
        } catch (Exception e) {
            log.error("Error completing order ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to complete order", e.getMessage()));
        }
    }

    // Enhanced search and filter endpoints
    /**
     * GET /api/orders/search - Search orders
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> searchOrders(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Searching orders with term: {}, page: {}, size: {}", searchTerm, page, size);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDto> orders = orderService.searchOrders(searchTerm, vendorId, pageable);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Orders search completed successfully", orders));

        } catch (Exception e) {
            log.error("Error searching orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to search orders", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/filter - Filter orders by criteria
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> filterOrders(
            @ModelAttribute OrderFilterRequestDto filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Filtering orders with request: {}, page: {}, size: {}", filterRequest, page, size);

        try {
            Long vendorId = getCurrentVendorIdRequired();
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderDto> orders = orderService.filterOrders(filterRequest, vendorId, pageable);

            return ResponseEntity.ok(ApiResponseDto.success(
                    "Orders filtered successfully", orders));

        } catch (Exception e) {
            log.error("Error filtering orders: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponseDto.error("Failed to filter orders", e.getMessage()));
        }
    }
}