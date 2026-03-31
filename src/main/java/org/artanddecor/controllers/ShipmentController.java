package org.artanddecor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.*;
import org.artanddecor.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Shipment Management REST Controller - Redesigned per Requirements
 * Provides streamlined endpoints for shipping system management
 * Focus on essential CRUD operations with proper role-based access control
 */
@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Shipment Management", description = "Streamlined shipping system APIs")
@CrossOrigin(origins = "*")
public class ShipmentController {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);

    private final ShipmentService shipmentService;
    private final ShipmentStateService shipmentStateService;
    private final ShippingFeeService shippingFeeService;
    private final ShippingFeeTypeService shippingFeeTypeService;

    // =============================================
    // SHIPPING_FEE_TYPE ENDPOINTS
    // =============================================

    /**
     * Get shipping fee types with filtering and pagination
     * Role: permitAll (both ADMIN and CUSTOMER can access)
     */
    @GetMapping("/fee-types")
    @Operation(
        summary = "Get shipping fee types with filtering",
        description = "Retrieve shipping fee types with optional filtering and pagination. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipping fee types retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<Page<ShippingFeeTypeDto>>> getShippingFeeTypes(
            @Parameter(description = "Filter by shipping fee type ID") 
            @RequestParam(required = false) Long shippingFeeTypeId,
            
            @Parameter(description = "Filter by shipping fee type name") 
            @RequestParam(required = false) String shippingFeeTypeName,
            
            @Parameter(description = "Filter by enabled status") 
            @RequestParam(required = false) Boolean shippingFeeTypeEnabled,
            
            @Parameter(description = "Text search across name, display name, and remark") 
            @RequestParam(required = false) String textSearch,
            
            @Parameter(description = "Pagination settings")
            @PageableDefault(page = 0, size = 10, sort = "shippingFeeTypeName") Pageable pageable) {

        logger.info("Getting shipping fee types with criteria - typeId: {}, name: {}, enabled: {}, textSearch: {}",
                   shippingFeeTypeId, shippingFeeTypeName, shippingFeeTypeEnabled, textSearch);

        try {
            Page<ShippingFeeTypeDto> feeTypesPage = shippingFeeTypeService.searchShippingFeeTypesByCriteria(
                    shippingFeeTypeId, shippingFeeTypeName, shippingFeeTypeEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee types retrieved successfully", feeTypesPage));
        } catch (Exception e) {
            logger.error("Error retrieving shipping fee types: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipping fee types: " + e.getMessage()));
        }
    }

    /**
     * Create new shipping fee type
     * Role: ADMIN only
     */
    @PostMapping("/fee-types")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create shipping fee type",
        description = "Create a new shipping fee type configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeTypeDto>> createShippingFeeType(
            @Parameter(description = "Shipping fee type data to create")
            @Valid @RequestBody ShippingFeeTypeDto shippingFeeTypeDto) {

        logger.info("Creating new shipping fee type: {}", shippingFeeTypeDto.getShippingFeeTypeName());

        try {
            ShippingFeeTypeDto createdFeeType = shippingFeeTypeService.createShippingFeeType(shippingFeeTypeDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponseDto.success("Shipping fee type created successfully", createdFeeType));
        } catch (Exception e) {
            logger.error("Error creating shipping fee type: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to create shipping fee type: " + e.getMessage()));
        }
    }

    /**
     * Update shipping fee type
     * Role: ADMIN only
     */
    @PutMapping("/fee-types/{shippingFeeTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update shipping fee type",
        description = "Update existing shipping fee type configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeTypeDto>> updateShippingFeeType(
            @Parameter(description = "Shipping fee type ID")
            @PathVariable Long shippingFeeTypeId,
            
            @Parameter(description = "Updated shipping fee type data")
            @Valid @RequestBody ShippingFeeTypeDto shippingFeeTypeDto) {

        logger.info("Updating shipping fee type ID: {}", shippingFeeTypeId);

        try {
            ShippingFeeTypeDto updatedFeeType = shippingFeeTypeService.updateShippingFeeType(shippingFeeTypeId, shippingFeeTypeDto);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee type updated successfully", updatedFeeType));
        } catch (Exception e) {
            logger.error("Error updating shipping fee type {}: {}", shippingFeeTypeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipping fee type: " + e.getMessage()));
        }
    }

    // =============================================
    // SHIPPING_FEE ENDPOINTS  
    // =============================================

    /**
     * Get shipping fees with filtering and pagination
     * Role: permitAll (both ADMIN and CUSTOMER can access)
     */
    @GetMapping("/fees")
    @Operation(
        summary = "Get shipping fees with filtering",
        description = "Retrieve shipping fees with optional filtering and pagination. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipping fees retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })  
    public ResponseEntity<BaseResponseDto<Page<ShippingFeeDto>>> getShippingFees(
            @Parameter(description = "Filter by shipping fee ID")
            @RequestParam(required = false) Long shippingFeeId,
            
            @Parameter(description = "Filter by shipping fee type ID")
            @RequestParam(required = false) Long shippingFeeTypeId,
            
            @Parameter(description = "Filter by minimum order price")
            @RequestParam(required = false) BigDecimal minOrderPrice,
            
            @Parameter(description = "Filter by maximum order price")
            @RequestParam(required = false) BigDecimal maxOrderPrice,
            
            @Parameter(description = "Filter by minimum shipping fee value")
            @RequestParam(required = false) BigDecimal minShippingFeeValue,
            
            @Parameter(description = "Filter by maximum shipping fee value")
            @RequestParam(required = false) BigDecimal maxShippingFeeValue,
            
            @Parameter(description = "Filter by enabled status")
            @RequestParam(required = false) Boolean shippingFeeEnabled,
            
            @Parameter(description = "Text search across display name and remark")
            @RequestParam(required = false) String textSearch,
            
            @Parameter(description = "Pagination settings")
            @PageableDefault(page = 0, size = 10, sort = "minOrderPrice") Pageable pageable) {

        logger.info("Getting shipping fees with criteria - feeId: {}, typeId: {}, enabled: {}", 
                   shippingFeeId, shippingFeeTypeId, shippingFeeEnabled);

        try {
            Page<ShippingFeeDto> feesPage = shippingFeeService.searchShippingFeesByCriteria(
                    shippingFeeId, shippingFeeTypeId, minOrderPrice, maxOrderPrice,
                    minShippingFeeValue, maxShippingFeeValue, shippingFeeEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fees retrieved successfully", feesPage));
        } catch (Exception e) {
            logger.error("Error retrieving shipping fees: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipping fees: " + e.getMessage()));
        }
    }

    /**
     * Create new shipping fee
     * Role: ADMIN only
     */
    @PostMapping("/fees")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create shipping fee",
        description = "Create a new shipping fee configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeDto>> createShippingFee(
            @Parameter(description = "Shipping fee data to create")
            @Valid @RequestBody ShippingFeeDto shippingFeeDto) {

        logger.info("Creating new shipping fee for type ID: {}", shippingFeeDto.getShippingFeeTypeId());

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
     * Update shipping fee
     * Role: ADMIN only
     */
    @PutMapping("/fees/{shippingFeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update shipping fee",
        description = "Update existing shipping fee configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShippingFeeDto>> updateShippingFee(
            @Parameter(description = "Shipping fee ID")
            @PathVariable Long shippingFeeId,
            
            @Parameter(description = "Updated shipping fee data")
            @Valid @RequestBody ShippingFeeDto shippingFeeDto) {

        logger.info("Updating shipping fee ID: {}", shippingFeeId);

        try {
            ShippingFeeDto updatedFee = shippingFeeService.updateShippingFee(shippingFeeId, shippingFeeDto);
            return ResponseEntity.ok(BaseResponseDto.success("Shipping fee updated successfully", updatedFee));
        } catch (Exception e) {
            logger.error("Error updating shipping fee {}: {}", shippingFeeId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipping fee: " + e.getMessage()));
        }
    }

    // =============================================
    // SHIPMENT_STATE ENDPOINTS
    // =============================================

    /**
     * Get shipment states with filtering and pagination
     * Role: permitAll (both ADMIN and CUSTOMER can access)
     */
    @GetMapping("/states")
    @Operation(
        summary = "Get shipment states with filtering",
        description = "Retrieve shipment states with optional filtering and pagination. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipment states retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<Page<ShipmentStateDto>>> getShipmentStates(
            @Parameter(description = "Filter by shipment state ID")
            @RequestParam(required = false) Long shipmentStateId,
            
            @Parameter(description = "Filter by shipment state name")
            @RequestParam(required = false) String shipmentStateName,
            
            @Parameter(description = "Filter by enabled status")
            @RequestParam(required = false) Boolean shipmentStateEnabled,
            
            @Parameter(description = "Text search across name, display name, and remark")
            @RequestParam(required = false) String textSearch,
            
            @Parameter(description = "Pagination settings")
            @PageableDefault(page = 0, size = 10, sort = "shipmentStateName") Pageable pageable) {

        logger.info("Getting shipment states with criteria - stateId: {}, name: {}, enabled: {}",
                   shipmentStateId, shipmentStateName, shipmentStateEnabled);

        try {
            Page<ShipmentStateDto> statesPage = shipmentStateService.searchShipmentStatesByCriteria(
                    shipmentStateId, shipmentStateName, shipmentStateEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment states retrieved successfully", statesPage));
        } catch (Exception e) {
            logger.error("Error retrieving shipment states: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipment states: " + e.getMessage()));
        }
    }

    /**
     * Create new shipment state
     * Role: ADMIN only
     */
    @PostMapping("/states")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Create shipment state",
        description = "Create a new shipment state configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentStateDto>> createShipmentState(
            @Parameter(description = "Shipment state data to create")
            @Valid @RequestBody ShipmentStateDto shipmentStateDto) {

        logger.info("Creating new shipment state: {}", shipmentStateDto.getShipmentStateName());

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
     * Update shipment state
     * Role: ADMIN only
     */
    @PutMapping("/states/{shipmentStateId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update shipment state",
        description = "Update existing shipment state configuration. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentStateDto>> updateShipmentState(
            @Parameter(description = "Shipment state ID")
            @PathVariable Long shipmentStateId,
            
            @Parameter(description = "Updated shipment state data")
            @Valid @RequestBody ShipmentStateDto shipmentStateDto) {

        logger.info("Updating shipment state ID: {}", shipmentStateId);

        try {
            ShipmentStateDto updatedState = shipmentStateService.updateShipmentState(shipmentStateId, shipmentStateDto);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment state updated successfully", updatedState));
        } catch (Exception e) {
            logger.error("Error updating shipment state {}: {}", shipmentStateId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipment state: " + e.getMessage()));
        }
    }

    // =============================================
    // SHIPMENT ENDPOINTS (No CREATE - created during order checkout)
    // =============================================

    /**
     * Get shipments with filtering and pagination
     * Role: permitAll (both ADMIN and CUSTOMER can access)
     */
    @GetMapping
    @Operation(
        summary = "Get shipments with filtering",
        description = "Retrieve shipments with comprehensive filtering and pagination. No authentication required."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipments retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BaseResponseDto<Page<ShipmentDto>>> getShipments(
            @Parameter(description = "Filter by shipment ID")
            @RequestParam(required = false) Long shipmentId,
            
            @Parameter(description = "Filter by order ID")
            @RequestParam(required = false) Long orderId,
            
            @Parameter(description = "Filter by shipment code")
            @RequestParam(required = false) String shipmentCode,
            
            @Parameter(description = "Filter by shipment state ID")
            @RequestParam(required = false) Long shipmentStateId,
            
            @Parameter(description = "Filter by receiver name")
            @RequestParam(required = false) String receiverName,
            
            @Parameter(description = "Filter by receiver phone")
            @RequestParam(required = false) String receiverPhone,
            
            @Parameter(description = "Filter by city")
            @RequestParam(required = false) String city,
            
            @Parameter(description = "Filter by country")
            @RequestParam(required = false) String country,
            
            @Parameter(description = "Filter by minimum shipping fee")
            @RequestParam(required = false) BigDecimal minShippingFee,
            
            @Parameter(description = "Filter by maximum shipping fee")
            @RequestParam(required = false) BigDecimal maxShippingFee,
            
            @Parameter(description = "Text search across multiple fields")
            @RequestParam(required = false) String textSearch,
            
            @Parameter(description = "Pagination settings")
            @PageableDefault(page = 0, size = 10, sort = "createdDt",
                           direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        logger.info("Getting shipments with criteria - shipmentId: {}, orderId: {}, stateId: {}",
                   shipmentId, orderId, shipmentStateId);

        try {
            Page<ShipmentDto> shipmentsPage = shipmentService.searchShipmentsByCriteria(
                    shipmentId, orderId, shipmentCode, shipmentStateId,
                    receiverName, receiverPhone, city, country,
                    minShippingFee, maxShippingFee, null, null, null, null,
                    textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success("Shipments retrieved successfully", shipmentsPage));
        } catch (Exception e) {
            logger.error("Error retrieving shipments: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to retrieve shipments: " + e.getMessage()));
        }
    }

    /**
     * Update shipment
     * Role: ADMIN only
     */
    @PutMapping("/{shipmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update shipment",
        description = "Update existing shipment information. Admin access required."
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<BaseResponseDto<ShipmentDto>> updateShipment(
            @Parameter(description = "Shipment ID")
            @PathVariable Long shipmentId,
            
            @Parameter(description = "Updated shipment data")
            @Valid @RequestBody ShipmentDto shipmentDto) {

        logger.info("Updating shipment ID: {}", shipmentId);

        try {
            ShipmentDto updatedShipment = shipmentService.updateShipment(shipmentId, shipmentDto);
            return ResponseEntity.ok(BaseResponseDto.success("Shipment updated successfully", updatedShipment));
        } catch (Exception e) {
            logger.error("Error updating shipment {}: {}", shipmentId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest("Failed to update shipment: " + e.getMessage()));
        }
    }
}