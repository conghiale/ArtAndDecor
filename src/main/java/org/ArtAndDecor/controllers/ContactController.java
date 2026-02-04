package org.ArtAndDecor.controllers;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.BaseResponseDto;
import org.ArtAndDecor.dto.ContactDto;
import org.ArtAndDecor.services.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @GetMapping("/slug/{contactSlug}")
    public ResponseEntity<BaseResponseDto<ContactDto>> getContactBySlug(@PathVariable String contactSlug) {
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
     * Get all enabled contacts (customer view)
     * Public access for browsing contact list
     */
    @GetMapping("/public")
    public ResponseEntity<BaseResponseDto<List<ContactDto>>> getPublicContacts() {
        logger.info("Requesting all public (enabled) contacts");
        try {
            List<ContactDto> contacts = contactService.findAllEnabledContacts();
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Retrieved %d enabled contacts", contacts.size()),
                    contacts));
        } catch (Exception e) {
            logger.error("Error retrieving public contacts: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                    "Failed to retrieve contacts: " + e.getMessage()));
        }
    }
    
    /**
     * Search contacts by name (customer search)
     * Public access for searching contacts
     */
    @GetMapping("/search")
    public ResponseEntity<BaseResponseDto<List<ContactDto>>> searchContacts(
            @RequestParam(name = "q", required = false) String searchTerm) {
        
        logger.info("Searching contacts with term: {}", searchTerm);
        
        try {
            if (searchTerm == null || searchTerm.isBlank()) {
                return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                        "Search term is required (minimum 2 characters)"));
            }
            
            if (searchTerm.length() < 2) {
                return ResponseEntity.badRequest().body(BaseResponseDto.badRequest(
                        "Search term must be at least 2 characters"));
            }
            
            List<ContactDto> results = contactService.searchContactsByName(searchTerm);
            return ResponseEntity.ok(BaseResponseDto.success(
                    String.format("Found %d matching contact(s)", results.size()),
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
    @GetMapping("/{contactId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<ContactDto>> getContactById(@PathVariable Long contactId) {
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
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<ContactDto>> createContact(@Valid @RequestBody ContactDto contactDto) {
        logger.info("Creating new contact: {}", contactDto.getContactName());
        try {
            ContactDto created = contactService.createContact(contactDto);
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
    @PutMapping("/{contactId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<ContactDto>> updateContact(
            @PathVariable Long contactId,
            @Valid @RequestBody ContactDto contactDto) {
        
        logger.info("Updating contact with ID: {}", contactId);
        try {
            ContactDto updated = contactService.updateContact(contactId, contactDto);
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
    @PatchMapping("/{contactId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<ContactDto>> updateContactStatus(
            @PathVariable Long contactId,
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
    @DeleteMapping("/{contactId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponseDto<Void>> deleteContact(@PathVariable Long contactId) {
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
     * Get total contact count (admin dashboard)
     * Requires ADMIN role
     */
    @GetMapping("/admin/total-count")
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
    

}
