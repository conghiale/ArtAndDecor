package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * Testimonial Request DTO for create and update operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String testimonialName;

    @NotBlank(message = "Quote is required")
    private String testimonialQuote;

    private Boolean testimonialEnabled;

    private Integer testimonialDisplayOrder;

    private Long imageId;
}
