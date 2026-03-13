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
     * Find contacts by multiple criteria
     * @param contactName Filter by contact name (exact match)
     * @param contactEnabled Filter by enabled status
     * @param textSearch Search text in name, slug, address, email, phone, fanpage, remark
     * @return List of matching contacts
     */
    @Query("SELECT c FROM Contact c WHERE " +
           "(:contactName IS NULL OR c.contactName = :contactName) AND " +
           "(:contactEnabled IS NULL OR c.contactEnabled = :contactEnabled) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(c.contactName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(c.contactSlug) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(c.contactAddress) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(c.contactEmail) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(c.contactPhone) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(c.contactFanpage) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(c.contactRemark) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    List<Contact> findContactsByCriteria(
        @Param("contactName") String contactName,
        @Param("contactEnabled") Boolean contactEnabled,
        @Param("textSearch") String textSearch);
    
    /**
     * Get all distinct contact names for dropdown/combobox
     * @return List of contact names
     */
    @Query("SELECT DISTINCT c.contactName FROM Contact c ORDER BY c.contactName")
    List<String> findAllContactNames();
}
