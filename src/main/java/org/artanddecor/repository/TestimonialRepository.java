package org.artanddecor.repository;

import org.artanddecor.model.Testimonial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Testimonial entity
 * Provides database operations for TESTIMONIAL table
 */
@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    /**
     * Find testimonials by criteria with pagination
     * @param testimonialEnabled Filter by enabled status (null = all)
     * @param textSearch Search text in name and quote fields
     * @param pageable Pagination parameters
     * @return Page of matching testimonials
     */
    @Query("SELECT t FROM Testimonial t WHERE " +
           "(:testimonialEnabled IS NULL OR t.testimonialEnabled = :testimonialEnabled) AND " +
           "(:textSearch IS NULL OR :textSearch = '' OR " +
           " LOWER(t.testimonialName) LIKE LOWER(CONCAT('%', :textSearch, '%')) OR " +
           " LOWER(t.testimonialQuote) LIKE LOWER(CONCAT('%', :textSearch, '%')))")
    Page<Testimonial> findByCriteria(
        @Param("testimonialEnabled") Boolean testimonialEnabled,
        @Param("textSearch") String textSearch,
        Pageable pageable);
}
