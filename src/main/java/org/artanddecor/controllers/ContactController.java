package org.artanddecor.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BaseResponseDto;
import org.artanddecor.dto.ContactDto;
import org.artanddecor.dto.ContactRequest;
import org.artanddecor.services.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Contact Management REST Controller
 * Handles HTTP requests for contact operations
 * - CUSTOMER APIs: Priority slug > name > ID
 * - ADMIN/MANAGER/STAFF APIs: Priority ID > name > slug
 */
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
@Tag(name = "Contact Management", description = "APIs for managing contact information including creation, updates, search and deletion")
public class ContactController {
    
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);
    private final ContactService contactService;
    
    // =============================================
    // PUBLIC ENDPOINTS - Customer Contact Browsing
    // =============================================
    
    /**
     * Get contact by slug (customer-friendly lookup)
     * Public access for finding contact information
     */
    @Operation(
        summary = "Get contact by slug", 
        description = "Retrieve contact information using URL-friendly slug. Public access for customer viewing."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Contact not found with the provided slug",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/slug/{contactSlug}")
    public ResponseEntity<BaseResponseDto<ContactDto>> getContactBySlug(
        @Parameter(description = "URL-friendly contact identifier", example = "art-store-hanoi")
        @PathVariable String contactSlug) {
        logger.info("Requesting contact by slug: {}", contactSlug);
        try {
            Optional<ContactDto> contact = contactService.findContactBySlug(contactSlug);
            return contact.map(contactDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Contact retrieved successfully",
                            contactDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Contact not found with slug: " + contactSlug)));
        } catch (Exception e) {
            logger.error("Error retrieving contact by slug {}: {}", contactSlug, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve contact: " + e.getMessage()));
        }
    }
    
    /**
     * Get contacts with criteria-based filtering and search with pagination
     * Public access for searching/browsing contacts
     */
    @Operation(
        summary = "Search contacts with criteria and pagination", 
        description = "Filter and search contacts by name, enabled status, and text search across multiple fields with pagination support. If no parameters provided, returns all contacts."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<BaseResponseDto<Page<ContactDto>>> getContactsByCriteria(
        @Parameter(description = "Filter by exact contact name", example = "Art Store Hanoi")
        @RequestParam(name = "contactName", required = false) String contactName,
        @Parameter(description = "Filter by enabled status (true/false)")
        @RequestParam(name = "contactEnabled", required = false) Boolean contactEnabled,
        @Parameter(description = "Search text in name, slug, address, email, phone, fanpage, remark fields", example = "hanoi")
        @RequestParam(name = "textSearch", required = false) String textSearch,
        @PageableDefault(page = 0, size = 10, sort = "contactName", direction = Sort.Direction.ASC) Pageable pageable) {
        
        logger.info("Searching contacts with criteria - name: {}, enabled: {}, textSearch: {}", 
                   contactName, contactEnabled, textSearch);
        
        try {
            Page<ContactDto> results = contactService.findContactsByCriteria(contactName, contactEnabled, textSearch, pageable);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d matching contact(s) on page %d", results.getTotalElements(), results.getNumber()),
                    results));
        } catch (Exception e) {
            logger.error("Error searching contacts: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to search contacts: " + e.getMessage()));
        }
    }
    
    // =============================================
    // ADMIN ENDPOINTS - Contact Management
    // =============================================
    
    /**
     * Get contact by ID (admin/management lookup)
     * Requires ADMIN role
     */
    @Operation(
        summary = "Get contact by ID", 
        description = "Retrieve contact information using database ID. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact found successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Contact not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class)))
    })
    @GetMapping("/{contactId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<ContactDto>> getContactById(
        @Parameter(description = "Contact database ID", example = "1")
        @PathVariable Long contactId) {
        logger.info("Admin requesting contact by ID: {}", contactId);
        try {
            Optional<ContactDto> contact = contactService.findContactById(contactId);
            return contact.map(contactDto -> ResponseEntity.ok(BaseResponseDto.success(
                            "Contact retrieved successfully",
                            contactDto)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            BaseResponseDto.notFound("Contact not found with ID: " + contactId)));
        } catch (Exception e) {
            logger.error("Error retrieving contact by ID {}: {}", contactId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve contact: " + e.getMessage()));
        }
    }
    

    
    /**
     * Create new contact
     * Requires ADMIN role
     */
    @Operation(
        summary = "Create new contact", 
        description = "Create a new contact entry with provided information. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contact created successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or invalid request data",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<ContactDto>> createContact(
        @Parameter(description = "Contact information to create", required = true)
        @Valid @RequestBody ContactRequest contactRequest) {
        logger.info("Creating new contact: {}", contactRequest.getContactName());
        try {
            ContactDto created = contactService.createContact(contactRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.success(
                    "Contact created successfully",
                    created));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error creating contact: {}", e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Validation error: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating contact: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to create contact: " + e.getMessage()));
        }
    }
    
    /**
     * Update contact
     * Requires ADMIN role
     */
    @Operation(
        summary = "Update existing contact", 
        description = "Update contact information by ID. Auto-generates slug if not provided. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Contact not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Validation error or invalid request data",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PutMapping("/{contactId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<ContactDto>> updateContact(
        @Parameter(description = "Contact ID to update", example = "1")
        @PathVariable Long contactId,
        @Parameter(description = "Updated contact information", required = true)
        @Valid @RequestBody ContactRequest contactRequest) {
        
        logger.info("Updating contact with ID: {}", contactId);
        try {
            ContactDto updated = contactService.updateContact(contactId, contactRequest);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Contact updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Validation error updating contact {}: {}", contactId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating contact {}: {}", contactId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update contact: " + e.getMessage()));
        }
    }
    
    /**
     * Update contact status (enable/disable)
     * Requires ADMIN role
     */
    @Operation(
        summary = "Update contact status", 
        description = "Enable or disable a contact by changing its status. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact status updated successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Contact not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @PatchMapping("/{contactId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<ContactDto>> updateContactStatus(
        @Parameter(description = "Contact ID to update", example = "1")
        @PathVariable Long contactId,
        @Parameter(description = "New enabled status (true/false)", required = true)
        @RequestParam Boolean enabled) {
        
        logger.info("Updating contact status - ID: {}, Enabled: {}", contactId, enabled);
        try {
            ContactDto updated = contactService.updateContactStatus(contactId, enabled);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Contact status updated successfully",
                    updated));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating contact status {}: {}", contactId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating contact status {}: {}", contactId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to update contact status: " + e.getMessage()));
        }
    }
    
    /**
     * Delete contact
     * Requires ADMIN role only
     */
    @Operation(
        summary = "Delete contact", 
        description = "Permanently delete a contact from the system. This action cannot be undone. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact deleted successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Contact not found with the provided ID",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or system error",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @DeleteMapping("/{contactId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<Void>> deleteContact(
        @Parameter(description = "Contact ID to delete", example = "1")
        @PathVariable Long contactId) {
        logger.info("Deleting contact with ID: {}", contactId);
        try {
            contactService.deleteContact(contactId);
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Contact deleted successfully",
                    null));
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting contact {}: {}", contactId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    BaseResponseDto.notFound(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting contact {}: {}", contactId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    BaseResponseDto.serverError("Failed to delete contact: " + e.getMessage()));
        }
    }
    
    /**
     * Get total contact count (dashboard statistics)
     * Requires ADMIN role
     */
    @Operation(
        summary = "Get total contact count", 
        description = "Retrieve the total number of contacts in the system for dashboard statistics. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total count retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "System error occurred",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @GetMapping("/stats/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<Long>> getTotalContactCount() {
        logger.info("Getting total contact count");
        try {
            long count = contactService.getTotalContactCount();
            return ResponseEntity.ok(BaseResponseDto.success(
                    "Total count retrieved successfully",
                    count));
        } catch (Exception e) {
            logger.error("Error getting total contact count: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to get total count: " + e.getMessage()));
        }
    }
    
    /**
     * Get all contact names for dropdown/combobox (management)
     * Requires ADMIN role
     */
    @Operation(
        summary = "Get all contact names", 
        description = "Retrieve list of all contact names for use in dropdown/combobox UI elements. Restricted to admin users only.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact names retrieved successfully",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "System error occurred",
            content = @Content(schema = @Schema(implementation = BaseResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    @GetMapping("/names")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<List<String>>> getAllContactNames() {
        logger.info("Getting all contact names");
        try {
            List<String> names = contactService.getAllContactNames();
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Retrieved %d contact names", names.size()),
                    names));
        } catch (Exception e) {
            logger.error("Error retrieving contact names: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve contact names: " + e.getMessage()));
        }
    }
    

}
