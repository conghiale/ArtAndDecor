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

    @NotBlank(message = "Tên là bắt buộc")
    @Size(max = 150, message = "Tên không được vượt quá 150 ký tự")
    private String testimonialName;

    @NotBlank(message = "Trích dẫn là bắt buộc")
    private String testimonialQuote;

    private Boolean testimonialEnabled;

    private Integer testimonialDisplayOrder;

    private Long imageId;
}
