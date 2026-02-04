package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.ContactDto;
import java.util.List;
import java.util.Optional;

/**
 * Contact Service Interface
 * Business logic operations for Contact management
 */
public interface ContactService {
    
    /**
     * Get contact by slug (customer-friendly lookup)
     * @param contactSlug the contact slug
     * @return Optional containing ContactDto if found
     */
    Optional<ContactDto> findContactBySlug(String contactSlug);
    
    /**
     * Get contact by ID (admin/system lookup)
     * @param contactId the contact ID
     * @return Optional containing ContactDto if found
     */
    Optional<ContactDto> findContactById(Long contactId);
    
    /**
     * Get contact by name
     * @param contactName the contact name
     * @return Optional containing ContactDto if found
     */
    Optional<ContactDto> findContactByName(String contactName);
    
    /**
     * Get contact by email
     * @param contactEmail the contact email
     * @return Optional containing ContactDto if found
     */
    Optional<ContactDto> findContactByEmail(String contactEmail);
    
    /**
     * Get all enabled contacts (public-facing)
     * @return List of enabled ContactDto objects
     */
    List<ContactDto> findAllEnabledContacts();
    

    
    /**
     * Search contacts by name (case-insensitive)
     * @param namePattern the name pattern to search
     * @return List of matching ContactDto objects
     */
    List<ContactDto> searchContactsByName(String namePattern);
    
    /**
     * Create new contact
     * @param contactDto the contact DTO with data
     * @return created ContactDto with ID
     * @throws IllegalArgumentException if validation fails or contact already exists
     */
    ContactDto createContact(ContactDto contactDto);
    
    /**
     * Update existing contact
     * @param contactId the contact ID to update
     * @param contactDto the contact DTO with updated data
     * @return updated ContactDto
     * @throws IllegalArgumentException if contact not found or validation fails
     */
    ContactDto updateContact(Long contactId, ContactDto contactDto);
    
    /**
     * Update contact status (enable/disable)
     * @param contactId the contact ID to update
     * @param enabled the new enabled status
     * @return updated ContactDto
     * @throws IllegalArgumentException if contact not found
     */
    ContactDto updateContactStatus(Long contactId, Boolean enabled);
    
    /**
     * Delete contact by ID
     * @param contactId the contact ID to delete
     * @throws IllegalArgumentException if contact not found
     */
    void deleteContact(Long contactId);
    

    
    /**
     * Get total count of contacts
     * @return total count of contacts in database
     */
    long getTotalContactCount();
}
