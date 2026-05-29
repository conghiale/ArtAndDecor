package org.artanddecor.services;

import org.artanddecor.dto.TestimonialDto;
import org.artanddecor.dto.TestimonialRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Testimonial Service Interface
 * Business logic operations for Testimonial management
 */
public interface TestimonialService {

    /**
     * Create a new testimonial
     * @param request testimonial creation data
     * @return created TestimonialDto
     */
    TestimonialDto createTestimonial(TestimonialRequest request);

    /**
     * Update an existing testimonial
     * @param testimonialId the testimonial ID to update
     * @param request updated testimonial data
     * @return updated TestimonialDto
     */
    TestimonialDto updateTestimonial(Long testimonialId, TestimonialRequest request);

    /**
     * Get a testimonial by ID
     * @param testimonialId the testimonial ID
     * @return TestimonialDto
     */
    TestimonialDto getTestimonialById(Long testimonialId);

    /**
     * Find testimonials by criteria with pagination
     * @param testimonialEnabled Filter by enabled status (null = all)
     * @param textSearch Search text in name and quote fields
     * @param pageable Pagination information
     * @return Page of matching TestimonialDto objects
     */
    Page<TestimonialDto> getTestimonialsByCriteria(Boolean testimonialEnabled, String textSearch, Pageable pageable);
}
