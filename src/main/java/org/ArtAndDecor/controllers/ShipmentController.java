package org.ArtAndDecor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.*;
import org.ArtAndDecor.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Shipment Management REST Controller - Complete CRUD APIs 
 * Provides comprehensive endpoints for SHIPPING SYSTEM TABLES management
 * Following the same pattern as OrderController with role-based access control
 */
@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Shipment Management", description = "Complete Shipment System APIs for shipments, states, fees, and fee types")
@CrossOrigin(origins = "*")
public class ShipmentController {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);

    private final ShipmentService shipmentService;
    private final ShipmentStateService shipmentStateService;  
    private final ShippingFeeService shippingFeeService;
    private final ShippingFeeTypeService shippingFeeTypeService;

    // =============================================
    // CUSTOMER SHIPMENT APIs (4 endpoints)
    // =============================================

    /**
     * API 1: Get My Shipments
     * Role: CUSTOMER - Get shipments for customer's orders
     */
    @GetMapping("/my-shipments")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
        summary = "Get my shipments",
        description = "Customer retrieves all shipments for their orders with pagination and filtering",
        tags = {"Customer Shipment Management"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "My shipments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Access denied - CUSTOMER role required"
        )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Page<ShipmentDto>>> getMyShipments(
            @Parameter(description = "Filter by shipment state") @RequestParam(required = false) Long shipmentStateId,
            @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
            @Parameter(description = "Filter shipments shipped after this date") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime shippedAfter,
            @Parameter(description = "Filter shipments delivered before this date")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveredBefore,
            @Parameter(description = "Text search") @RequestParam(required = false) String textSearch,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        try {
            // In real implementation, get user ID from authentication
            // Long userId = ((UserDetails) authentication.getPrincipal()).getUserId();
            // For now, we'll search all and filter by user ownership in service layer
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDt"));
            
            Page<ShipmentDto> shipments = shipmentService.searchShipmentsByCriteria(
                null, null, null, shipmentStateId, null, null,
                city, null, null, null, shippedAfter, null,
                null, deliveredBefore, textSearch, pageable
            );

            return ResponseEntity.ok(BaseResponseDto.success("My shipments retrieved successfully", shipments));
        } catch (Exception e) {
            logger.error("Error retrieving customer shipments: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipments: " + e.getMessage()));
        }
    }

    /**
     * API 2: Get My Shipment Details
     * Role: CUSTOMER - Get specific shipment details 
     */
    @GetMapping("/my-shipments/{shipmentId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
        summary = "Get my shipment details",
        description = "Customer retrieves detailed information about their specific shipment",
        tags = {"Customer Shipment Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentDto>> getMyShipmentDetails(
            @Parameter(description = "Shipment ID", required = true) @PathVariable Long shipmentId,
            Authentication authentication) {

        try {
            ShipmentDto shipment = shipmentService.getShipmentById(shipmentId);
            // TODO: Add validation that shipment belongs to authenticated user
            return ResponseEntity.ok(BaseResponseDto.success("Shipment details retrieved successfully", shipment));
        } catch (Exception e) {
            logger.error("Error retrieving shipment details {}: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDto.notFound("Shipment not found: " + e.getMessage()));
        }
    }

    /**
     * API 3: Track Shipment by Code
     * Role: PUBLIC - Track shipment using shipment code (no authentication required)
     */
    @GetMapping("/track/{shipmentCode}")
    @Operation(
        summary = "Track shipment by code",
        description = "Public API to track shipment status using shipment code without authentication",
        tags = {"Public Shipment Tracking"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Shipment tracking information retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Shipment not found with provided code"
        )
    })
    public ResponseEntity<BaseResponseDto<ShipmentDto>> trackShipmentByCode(
            @Parameter(description = "Shipment tracking code", required = true) @PathVariable String shipmentCode) {

        try {
            ShipmentDto shipment = shipmentService.getShipmentByCode(shipmentCode);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment tracking information retrieved successfully", shipment));
        } catch (Exception e) {
            logger.error("Error tracking shipment by code {}: {}", shipmentCode, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDto.notFound("Shipment not found: " + e.getMessage()));
        }
    }

    /**
     * API 4: Calculate Shipping Fee
     * Role: PUBLIC - Calculate shipping fee for order amount
     */
    @GetMapping("/calculate-shipping-fee")
    @Operation(
        summary = "Calculate shipping fee",
        description = "Calculate applicable shipping fee for given order amount",
        tags = {"Public Shipping Calculator"}
    )
    public ResponseEntity<BaseResponseDto<ShippingFeeDto>> calculateShippingFee(
            @Parameter(description = "Order amount to calculate shipping fee", required = true) 
            @RequestParam BigDecimal orderAmount) {

        try {
            ShippingFeeDto shippingFee = shippingFeeService.calculateShippingFee(orderAmount);
            if (shippingFee == null) {
                return ResponseEntity.ok(BaseResponseDto.success("No applicable shipping fee found for order amount", null));
            }
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee calculated successfully", shippingFee));
        } catch (Exception e) {
            logger.error("Error calculating shipping fee for amount {}: {}", orderAmount, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to calculate shipping fee: " + e.getMessage()));
        }
    }

    // =============================================
    // ADMIN SHIPMENT MANAGEMENT APIs (8 endpoints)
    // =============================================

    /**
     * API 5: Admin - Get All Shipments
     * Role: ADMIN/MANAGER - Get all shipments with advanced filtering
     */
    @GetMapping("/management")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin - Get all shipments",
        description = "Admin retrieves all shipments with comprehensive filtering and pagination",
        tags = {"Admin Shipment Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Page<ShipmentDto>>> getAllShipments(
            @Parameter(description = "Filter by shipment ID") @RequestParam(required = false) Long shipmentId,
            @Parameter(description = "Filter by order ID") @RequestParam(required = false) Long orderId,
            @Parameter(description = "Filter by shipment code") @RequestParam(required = false) String shipmentCode,
            @Parameter(description = "Filter by shipment state ID") @RequestParam(required = false) Long shipmentStateId,
            @Parameter(description = "Filter by receiver name") @RequestParam(required = false) String receiverName,
            @Parameter(description = "Filter by receiver phone") @RequestParam(required = false) String receiverPhone,
            @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
            @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
            @Parameter(description = "Filter by minimum shipping fee") @RequestParam(required = false) BigDecimal minShippingFee,
            @Parameter(description = "Filter by maximum shipping fee") @RequestParam(required = false) BigDecimal maxShippingFee,
            @Parameter(description = "Filter shipments shipped after this date") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime shippedAfter,
            @Parameter(description = "Filter shipments shipped before this date") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime shippedBefore,
            @Parameter(description = "Filter shipments delivered after this date") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveredAfter,
            @Parameter(description = "Filter shipments delivered before this date") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveredBefore,
            @Parameter(description = "Text search across shipment fields") @RequestParam(required = false) String textSearch,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdDt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        try {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<ShipmentDto> shipments = shipmentService.searchShipmentsByCriteria(
                    shipmentId, orderId, shipmentCode, shipmentStateId, receiverName, receiverPhone,
                    city, country, minShippingFee, maxShippingFee, shippedAfter, shippedBefore,
                    deliveredAfter, deliveredBefore, textSearch, pageable);

            return ResponseEntity.ok(BaseResponseDto.success("Shipments retrieved successfully", shipments));
        } catch (Exception e) {
            logger.error("Error retrieving shipments: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipments: " + e.getMessage()));
        }
    }

    /**
     * API 6: Admin - Get Shipment Details 
     * Role: ADMIN/MANAGER - Get detailed shipment information
     */
    @GetMapping("/management/{shipmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin - Get shipment details",
        description = "Admin retrieves detailed information about any shipment",
        tags = {"Admin Shipment Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentDto>> getShipmentById(
            @Parameter(description = "Shipment ID", required = true) @PathVariable Long shipmentId) {

        try {
            ShipmentDto shipment = shipmentService.getShipmentById(shipmentId);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment retrieved successfully", shipment));
        } catch (Exception e) {
            logger.error("Error retrieving shipment {}: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponseDto.notFound("Shipment not found: " + e.getMessage()));
        }
    }

    /**
     * API 7: Admin - Create Shipment
     * Role: ADMIN/MANAGER - Create new shipment for an order
     */
    @PostMapping("/management")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin - Create shipment",
        description = "Admin creates a new shipment for an order with shipping details",
        tags = {"Admin Shipment Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentDto>> createShipment(
            @Parameter(
                description = "Shipment creation request with order ID, shipping address, and fee details",
                required = true
            )
            @Valid @RequestBody ShipmentDto shipmentDto) {

        try {
            ShipmentDto createdShipment = shipmentService.createShipment(shipmentDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Shipment created successfully", createdShipment));
        } catch (Exception e) {
            logger.error("Error creating shipment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create shipment: " + e.getMessage()));
        }
    }

    /**
     * API 8: Admin - Update Shipment
     * Role: ADMIN/MANAGER - Update shipment details
     */
    @PutMapping("/management/{shipmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin - Update shipment",
        description = "Admin updates shipment information including shipping address and status",
        tags = {"Admin Shipment Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentDto>> updateShipment(
            @Parameter(description = "Shipment ID", required = true) @PathVariable Long shipmentId,
            @Parameter(description = "Updated shipment information", required = true) 
            @Valid @RequestBody ShipmentDto shipmentDto) {

        try {
            ShipmentDto updatedShipment = shipmentService.updateShipment(shipmentId, shipmentDto);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment updated successfully", updatedShipment));
        } catch (Exception e) {
            logger.error("Error updating shipment {}: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipment: " + e.getMessage()));
        }
    }

    /**
     * API 9: Admin - Update Shipment State
     * Role: ADMIN/MANAGER - Change shipment state (PREPARING → SHIPPED → IN_TRANSIT → DELIVERED)
     */
    @PutMapping("/management/{shipmentId}/state")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin - Update shipment state",
        description = "Admin updates shipment state with optional remark for state transition tracking",
        tags = {"Admin Shipment Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentDto>> updateShipmentState(
            @Parameter(description = "Shipment ID", required = true) @PathVariable Long shipmentId,
            @Parameter(description = "New shipment state ID", required = true) @RequestParam Long shipmentStateId,
            @Parameter(description = "Optional remark for state change") @RequestParam(required = false) String remark) {

        try {
            ShipmentDto updatedShipment = shipmentService.updateShipmentState(shipmentId, shipmentStateId, remark);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment state updated successfully", updatedShipment));
        } catch (Exception e) {
            logger.error("Error updating shipment state for {}: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipment state: " + e.getMessage()));
        }
    }

    /**
     * API 10: Admin - Mark as Shipped
     * Role: ADMIN/MANAGER - Mark shipment as shipped with timestamp
     */
    @PutMapping("/management/{shipmentId}/shipped")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin - Mark shipment as shipped", 
        description = "Admin marks shipment as shipped with optional custom shipped datetime and remark",
        tags = {"Admin Shipment Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentDto>> markAsShipped(
            @Parameter(description = "Shipment ID", required = true) @PathVariable Long shipmentId,
            @Parameter(description = "Shipped datetime (optional, defaults to now)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime shippedAt,
            @Parameter(description = "Optional remark for shipped status") @RequestParam(required = false) String remark) {

        try {
            ShipmentDto updatedShipment = shipmentService.markAsShipped(shipmentId, shippedAt, remark);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment marked as shipped successfully", updatedShipment));
        } catch (Exception e) {
            logger.error("Error marking shipment {} as shipped: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to mark shipment as shipped: " + e.getMessage()));
        }
    }

    /**
     * API 11: Admin - Mark as Delivered
     * Role: ADMIN/MANAGER - Mark shipment as delivered with timestamp  
     */
    @PutMapping("/management/{shipmentId}/delivered")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Admin - Mark shipment as delivered",
        description = "Admin marks shipment as delivered with optional custom delivered datetime and remark",
        tags = {"Admin Shipment Management"}
    )  
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentDto>> markAsDelivered(
            @Parameter(description = "Shipment ID", required = true) @PathVariable Long shipmentId,
            @Parameter(description = "Delivered datetime (optional, defaults to now)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveredAt,
            @Parameter(description = "Optional remark for delivered status") @RequestParam(required = false) String remark) {

        try {
            ShipmentDto updatedShipment = shipmentService.markAsDelivered(shipmentId, deliveredAt, remark);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment marked as delivered successfully", updatedShipment)); 
        } catch (Exception e) {
            logger.error("Error marking shipment {} as delivered: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to mark shipment as delivered: " + e.getMessage()));
        }
    }

    /**
     * API 12: Admin - Delete Shipment
     * Role: ADMIN - Delete shipment (restricted to ADMIN only)
     */
    @DeleteMapping("/management/{shipmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Delete shipment",
        description = "Admin deletes a shipment record (ADMIN only - use with caution)",
        tags = {"Admin Shipment Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Void>> deleteShipment(
            @Parameter(description = "Shipment ID", required = true) @PathVariable Long shipmentId) {

        try {
            shipmentService.deleteShipment(shipmentId);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting shipment {}: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to delete shipment: " + e.getMessage()));
        }
    }

    // =============================================
    // SHIPMENT STATE MANAGEMENT APIs (6 endpoints)
    // =============================================

    /**
     * API 13: Get All Shipment States
     * Role: PUBLIC - Get all available shipment states
     */
    @GetMapping("/states")
    @Operation(
        summary = "Get all shipment states",
        description = "Retrieves all available shipment states with filtering and pagination",
        tags = {"Shipment State Management"}
    )
    public ResponseEntity<BaseResponseDto<Page<ShipmentStateDto>>> getAllShipmentStates(
            @Parameter(description = "Filter by state ID") @RequestParam(required = false) Long shipmentStateId,
            @Parameter(description = "Filter by state name") @RequestParam(required = false) String shipmentStateName,
            @Parameter(description = "Filter by enabled status") @RequestParam(required = false) Boolean shipmentStateEnabled,
            @Parameter(description = "Text search") @RequestParam(required = false) String textSearch,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "shipmentStateName"));
            
            Page<ShipmentStateDto> states = shipmentStateService.searchShipmentStatesByCriteria(
                shipmentStateId, shipmentStateName, shipmentStateEnabled, textSearch, pageable
            );

            return ResponseEntity.ok(BaseResponseDto.success("Shipment states retrieved successfully", states));
        } catch (Exception e) {
            logger.error("Error retrieving shipment states: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipment states: " + e.getMessage()));
        }
    }

    /**
     * API 14: Admin - Create Shipment State
     * Role: ADMIN - Create new shipment state
     */
    @PostMapping("/states/management")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Create shipment state",
        description = "Admin creates a new shipment state configuration",
        tags = {"Admin Shipment State Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentStateDto>> createShipmentState(
            @Parameter(description = "Shipment state creation request", required = true)
            @Valid @RequestBody ShipmentStateDto shipmentStateDto) {

        try {
            ShipmentStateDto createdState = shipmentStateService.createShipmentState(shipmentStateDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Shipment state created successfully", createdState));
        } catch (Exception e) {
            logger.error("Error creating shipment state: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create shipment state: " + e.getMessage()));
        }
    }

    /**
     * API 15: Admin - Update Shipment State
     * Role: ADMIN - Update shipment state configuration
     */
    @PutMapping("/states/management/{shipmentStateId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Update shipment state",
        description = "Admin updates shipment state configuration details",
        tags = {"Admin Shipment State Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentStateDto>> updateShipmentState(
            @Parameter(description = "Shipment state ID", required = true) @PathVariable Long shipmentStateId,
            @Parameter(description = "Updated shipment state information", required = true)
            @Valid @RequestBody ShipmentStateDto shipmentStateDto) {

        try {
            ShipmentStateDto updatedState = shipmentStateService.updateShipmentState(shipmentStateId, shipmentStateDto);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment state updated successfully", updatedState));
        } catch (Exception e) {
            logger.error("Error updating shipment state {}: {}", shipmentStateId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipment state: " + e.getMessage()));
        }
    }

    /**
     * API 16: Admin - Toggle Shipment State
     * Role: ADMIN - Enable/disable shipment state
     */
    @PutMapping("/states/management/{shipmentStateId}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Toggle shipment state",
        description = "Admin toggles shipment state enabled/disabled status",
        tags = {"Admin Shipment State Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentStateDto>> toggleShipmentState(
            @Parameter(description = "Shipment state ID", required = true) @PathVariable Long shipmentStateId) {

        try {
            ShipmentStateDto toggledState = shipmentStateService.toggleShipmentStateEnabled(shipmentStateId);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment state toggled successfully", toggledState));
        } catch (Exception e) {
            logger.error("Error toggling shipment state {}: {}", shipmentStateId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to toggle shipment state: " + e.getMessage()));
        }
    }

    /**
     * API 17: Get Enabled Shipment States
     * Role: PUBLIC - Get all enabled shipment states for use in forms/dropdowns
     */
    @GetMapping("/states/enabled")
    @Operation(
        summary = "Get enabled shipment states",
        description = "Retrieves all enabled shipment states for dropdown lists and form selections",
        tags = {"Shipment State Management"}
    )
    public ResponseEntity<BaseResponseDto<List<ShipmentStateDto>>> getEnabledShipmentStates() {

        try {
            List<ShipmentStateDto> enabledStates = shipmentStateService.getAllEnabledShipmentStates();
            return ResponseEntity.ok(BaseResponseDto.success("Enabled shipment states retrieved successfully", enabledStates));
        } catch (Exception e) {
            logger.error("Error retrieving enabled shipment states: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve enabled shipment states: " + e.getMessage()));
        }
    }

    /**
     * API 18: Admin - Delete Shipment State
     * Role: ADMIN - Delete shipment state (use with caution)
     */
    @DeleteMapping("/admin/states/{shipmentStateId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Delete shipment state",
        description = "Admin deletes a shipment state configuration (ADMIN only - use with extreme caution)",
        tags = {"Admin Shipment State Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Void>> deleteShipmentState(
            @Parameter(description = "Shipment state ID", required = true) @PathVariable Long shipmentStateId) {

        try {
            shipmentStateService.deleteShipmentState(shipmentStateId);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment state deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting shipment state {}: {}", shipmentStateId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to delete shipment state: " + e.getMessage()));
        }
    }

    // =============================================
    // SHIPPING FEE MANAGEMENT APIs (6 endpoints)
    // =============================================

    /**
     * API 19: Get All Shipping Fees
     * Role: PUBLIC - Get shipping fee configurations with filtering
     */
    @GetMapping("/fees")
    @Operation(
        summary = "Get all shipping fees",
        description = "Retrieves shipping fee configurations with filtering and pagination",
        tags = {"Shipping Fee Management"}
    )
    public ResponseEntity<BaseResponseDto<Page<ShippingFeeDto>>> getAllShippingFees(
            @Parameter(description = "Filter by shipping fee ID") @RequestParam(required = false) Long shippingFeeId,
            @Parameter(description = "Filter by shipping fee type ID") @RequestParam(required = false) Long shippingFeeTypeId,
            @Parameter(description = "Filter by minimum order price") @RequestParam(required = false) BigDecimal minOrderPrice,
            @Parameter(description = "Filter by maximum order price") @RequestParam(required = false) BigDecimal maxOrderPrice,
            @Parameter(description = "Filter by minimum shipping fee value") @RequestParam(required = false) BigDecimal minShippingFeeValue,
            @Parameter(description = "Filter by maximum shipping fee value") @RequestParam(required = false) BigDecimal maxShippingFeeValue,
            @Parameter(description = "Filter by enabled status") @RequestParam(required = false) Boolean shippingFeeEnabled,
            @Parameter(description = "Text search") @RequestParam(required = false) String textSearch,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "minOrderPrice"));
            
            Page<ShippingFeeDto> fees = shippingFeeService.searchShippingFeesByCriteria(
                shippingFeeId, shippingFeeTypeId, minOrderPrice, maxOrderPrice,
                minShippingFeeValue, maxShippingFeeValue, shippingFeeEnabled, textSearch, pageable
            );

            return ResponseEntity.ok(BaseResponseDto.success("Shipping fees retrieved successfully", fees));
        } catch (Exception e) {
            logger.error("Error retrieving shipping fees: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipping fees: " + e.getMessage()));
        }
    }

    /**
     * API 20: Get Applicable Shipping Fees  
     * Role: PUBLIC - Get applicable shipping fees for specific order amount
     */
    @GetMapping("/fees/applicable")
    @Operation(
        summary = "Get applicable shipping fees",
        description = "Retrieves all applicable shipping fee options for given order amount",
        tags = {"Shipping Fee Management"}
    )
    public ResponseEntity<BaseResponseDto<List<ShippingFeeDto>>> getApplicableShippingFees(
            @Parameter(description = "Order amount to find applicable shipping fees", required = true)
            @RequestParam BigDecimal orderAmount) {

        try {
            List<ShippingFeeDto> applicableFees = shippingFeeService.getApplicableShippingFees(orderAmount);
            return ResponseEntity.ok(BaseResponseDto.success("Applicable shipping fees retrieved successfully", applicableFees));
        } catch (Exception e) {
            logger.error("Error retrieving applicable shipping fees for amount {}: {}", orderAmount, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve applicable shipping fees: " + e.getMessage()));
        }
    }

    /**
     * API 21: Admin - Create Shipping Fee
     * Role: ADMIN - Create new shipping fee configuration
     */
    @PostMapping("/admin/fees")  
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Create shipping fee",
        description = "Admin creates a new shipping fee configuration with price range and fee amount",
        tags = {"Admin Shipping Fee Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeDto>> createShippingFee(
            @Parameter(description = "Shipping fee creation request", required = true)
            @Valid @RequestBody ShippingFeeDto shippingFeeDto) {

        try {
            ShippingFeeDto createdFee = shippingFeeService.createShippingFee(shippingFeeDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Shipping fee created successfully", createdFee));
        } catch (Exception e) {
            logger.error("Error creating shipping fee: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create shipping fee: " + e.getMessage()));
        }
    }

    /**
     * API 22: Admin - Update Shipping Fee
     * Role: ADMIN - Update shipping fee configuration
     */
    @PutMapping("/admin/fees/{shippingFeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Update shipping fee",
        description = "Admin updates shipping fee configuration including price ranges and fee amounts",
        tags = {"Admin Shipping Fee Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeDto>> updateShippingFee(
            @Parameter(description = "Shipping fee ID", required = true) @PathVariable Long shippingFeeId,
            @Parameter(description = "Updated shipping fee information", required = true)
            @Valid @RequestBody ShippingFeeDto shippingFeeDto) {

        try {
            ShippingFeeDto updatedFee = shippingFeeService.updateShippingFee(shippingFeeId, shippingFeeDto);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee updated successfully", updatedFee));
        } catch (Exception e) {
            logger.error("Error updating shipping fee {}: {}", shippingFeeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipping fee: " + e.getMessage()));
        }
    }

    /**
     * API 23: Admin - Toggle Shipping Fee
     * Role: ADMIN - Enable/disable shipping fee
     */
    @PutMapping("/admin/fees/{shippingFeeId}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Toggle shipping fee",
        description = "Admin toggles shipping fee enabled/disabled status",
        tags = {"Admin Shipping Fee Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeDto>> toggleShippingFee(
            @Parameter(description = "Shipping fee ID", required = true) @PathVariable Long shippingFeeId) {

        try {
            ShippingFeeDto toggledFee = shippingFeeService.toggleShippingFeeEnabled(shippingFeeId);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee toggled successfully", toggledFee));
        } catch (Exception e) {
            logger.error("Error toggling shipping fee {}: {}", shippingFeeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to toggle shipping fee: " + e.getMessage()));
        }
    }

    /**
     * API 24: Admin - Delete Shipping Fee
     * Role: ADMIN - Delete shipping fee configuration  
     */
    @DeleteMapping("/admin/fees/{shippingFeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Delete shipping fee",
        description = "Admin deletes a shipping fee configuration (ADMIN only)",
        tags = {"Admin Shipping Fee Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Void>> deleteShippingFee(
            @Parameter(description = "Shipping fee ID", required = true) @PathVariable Long shippingFeeId) {

        try {
            shippingFeeService.deleteShippingFee(shippingFeeId);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting shipping fee {}: {}", shippingFeeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to delete shipping fee: " + e.getMessage()));
        }
    }

    // =============================================
    // SHIPPING FEE TYPE MANAGEMENT APIs (6 endpoints)
    // =============================================

    /**
     * API 25: Get All Shipping Fee Types
     * Role: PUBLIC - Get shipping fee type configurations  
     */
    @GetMapping("/fee-types")
    @Operation(
        summary = "Get all shipping fee types",
        description = "Retrieves shipping fee type configurations with filtering and pagination",
        tags = {"Shipping Fee Type Management"}
    )
    public ResponseEntity<BaseResponseDto<Page<ShippingFeeTypeDto>>> getAllShippingFeeTypes(
            @Parameter(description = "Filter by fee type ID") @RequestParam(required = false) Long shippingFeeTypeId,
            @Parameter(description = "Filter by fee type name") @RequestParam(required = false) String shippingFeeTypeName,
            @Parameter(description = "Filter by enabled status") @RequestParam(required = false) Boolean shippingFeeTypeEnabled,
            @Parameter(description = "Text search") @RequestParam(required = false) String textSearch,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "shippingFeeTypeName"));
            
            Page<ShippingFeeTypeDto> types = shippingFeeTypeService.searchShippingFeeTypesByCriteria(
                shippingFeeTypeId, shippingFeeTypeName, shippingFeeTypeEnabled, textSearch, pageable
            );

            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee types retrieved successfully", types));
        } catch (Exception e) {
            logger.error("Error retrieving shipping fee types: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipping fee types: " + e.getMessage()));
        }
    }

    /**
     * API 26: Get Enabled Shipping Fee Types
     * Role: PUBLIC - Get enabled shipping fee types for forms/dropdowns
     */
    @GetMapping("/fee-types/enabled") 
    @Operation(
        summary = "Get enabled shipping fee types",
        description = "Retrieves all enabled shipping fee types for dropdown lists and form selections",
        tags = {"Shipping Fee Type Management"}
    )
    public ResponseEntity<BaseResponseDto<List<ShippingFeeTypeDto>>> getEnabledShippingFeeTypes() {

        try {
            List<ShippingFeeTypeDto> enabledTypes = shippingFeeTypeService.getAllEnabledShippingFeeTypes();
            return ResponseEntity.ok(BaseResponseDto.success("Enabled shipping fee types retrieved successfully", enabledTypes));
        } catch (Exception e) {
            logger.error("Error retrieving enabled shipping fee types: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve enabled shipping fee types: " + e.getMessage()));
        }
    }

    /**
     * API 27: Admin - Create Shipping Fee Type  
     * Role: ADMIN - Create new shipping fee type
     */
    @PostMapping("/admin/fee-types")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Create shipping fee type",
        description = "Admin creates a new shipping fee type configuration",
        tags = {"Admin Shipping Fee Type Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeTypeDto>> createShippingFeeType(
            @Parameter(description = "Shipping fee type creation request", required = true)
            @Valid @RequestBody ShippingFeeTypeDto shippingFeeTypeDto) {

        try {
            ShippingFeeTypeDto createdType = shippingFeeTypeService.createShippingFeeType(shippingFeeTypeDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Shipping fee type created successfully", createdType));
        } catch (Exception e) {
            logger.error("Error creating shipping fee type: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create shipping fee type: " + e.getMessage()));
        }
    }

    /**
     * API 28: Admin - Update Shipping Fee Type
     * Role: ADMIN - Update shipping fee type configuration
     */
    @PutMapping("/admin/fee-types/{shippingFeeTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Update shipping fee type",
        description = "Admin updates shipping fee type configuration details",
        tags = {"Admin Shipping Fee Type Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeTypeDto>> updateShippingFeeType(
            @Parameter(description = "Shipping fee type ID", required = true) @PathVariable Long shippingFeeTypeId,
            @Parameter(description = "Updated shipping fee type information", required = true)
            @Valid @RequestBody ShippingFeeTypeDto shippingFeeTypeDto) {

        try {
            ShippingFeeTypeDto updatedType = shippingFeeTypeService.updateShippingFeeType(shippingFeeTypeId, shippingFeeTypeDto);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee type updated successfully", updatedType));
        } catch (Exception e) {
            logger.error("Error updating shipping fee type {}: {}", shippingFeeTypeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipping fee type: " + e.getMessage()));
        }
    }

    /**
     * API 29: Admin - Toggle Shipping Fee Type
     * Role: ADMIN - Enable/disable shipping fee type
     */
    @PutMapping("/admin/fee-types/{shippingFeeTypeId}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Toggle shipping fee type",
        description = "Admin toggles shipping fee type enabled/disabled status",
        tags = {"Admin Shipping Fee Type Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeTypeDto>> toggleShippingFeeType(
            @Parameter(description = "Shipping fee type ID", required = true) @PathVariable Long shippingFeeTypeId) {

        try {
            ShippingFeeTypeDto toggledType = shippingFeeTypeService.toggleShippingFeeTypeEnabled(shippingFeeTypeId);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee type toggled successfully", toggledType));
        } catch (Exception e) {
            logger.error("Error toggling shipping fee type {}: {}", shippingFeeTypeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to toggle shipping fee type: " + e.getMessage()));
        }
    }

    /**
     * API 30: Admin - Delete Shipping Fee Type
     * Role: ADMIN - Delete shipping fee type configuration
     */
    @DeleteMapping("/admin/fee-types/{shippingFeeTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Admin - Delete shipping fee type",
        description = "Admin deletes a shipping fee type configuration (ADMIN only - use with extreme caution)",
        tags = {"Admin Shipping Fee Type Management"}
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<Void>> deleteShippingFeeType(
            @Parameter(description = "Shipping fee type ID", required = true) @PathVariable Long shippingFeeTypeId) {

        try {
            shippingFeeTypeService.deleteShippingFeeType(shippingFeeTypeId);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee type deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error deleting shipping fee type {}: {}", shippingFeeTypeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to delete shipping fee type: " + e.getMessage()));
        }
    }
}