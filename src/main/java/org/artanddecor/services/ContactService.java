package org.artanddecor.services;

import org.artanddecor.dto.ContactDto;
import org.artanddecor.dto.ContactRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Find contacts by criteria (with filters) - paginated
     * @param contactName Filter by contact name (exact match)
     * @param contactEnabled Filter by enabled status  
     * @param textSearch Search text in multiple fields
     * @param pageable Pagination parameters
     * @return Page of matching ContactDto objects
     */
    Page<ContactDto> findContactsByCriteria(String contactName, Boolean contactEnabled, String textSearch, Pageable pageable);
    
    /**
     * Get all contact names for dropdown/combobox
     * @return List of all contact names
     */
    List<String> getAllContactNames();
    

    
    /**
     * Create new contact
     * @param contactRequest the contact request data
     * @return created ContactDto with ID
     * @throws IllegalArgumentException if validation fails or contact already exists
     */
    ContactDto createContact(ContactRequest contactRequest);
    
    /**
     * Update existing contact
     * @param contactId the contact ID to update
     * @param contactRequest the contact request data with updated values
     * @return updated ContactDto
     * @throws IllegalArgumentException if contact not found or validation fails
     */
    ContactDto updateContact(Long contactId, ContactRequest contactRequest);
    
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
