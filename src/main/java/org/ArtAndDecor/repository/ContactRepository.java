package org.ArtAndDecor.repository;

import org.ArtAndDecor.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Contact entity
 * Provides database operations for CONTACT table
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    /**
     * Find contact by slug
     * @param contactSlug the contact slug
     * @return Optional containing Contact if found
     */
    Optional<Contact> findByContactSlug(String contactSlug);
    
    /**
     * Find contact by name
     * @param contactName the contact name
     * @return Optional containing Contact if found
     */
    Optional<Contact> findByContactName(String contactName);
    
    /**
     * Find contact by email
     * @param contactEmail the contact email
     * @return Optional containing Contact if found
     */
    Optional<Contact> findByContactEmail(String contactEmail);
    
    /**
     * Find all enabled contacts
     * @return List of enabled contacts
     */
    List<Contact> findByContactEnabledTrue();
    
    /**
     * Find contacts by enabled status
     * @param enabled the enabled status
     * @return List of contacts with specified enabled status
     */
    List<Contact> findByContactEnabled(Boolean enabled);
    

    
    /**
     * Find contacts by name pattern (case-insensitive)
     * @param namePattern the name pattern to search
     * @return List of matching contacts
     */
    @Query("SELECT c FROM Contact c WHERE LOWER(c.contactName) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    List<Contact> findByContactNameContainingIgnoreCase(@Param("namePattern") String namePattern);
}
