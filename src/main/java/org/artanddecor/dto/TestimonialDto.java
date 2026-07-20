package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Testimonial DTO for API responses
 * Contains full information from TESTIMONIAL table including related image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialDto {

    private Long testimonialId;

    @NotBlank(message = "Tên là bắt buộc")
    @Size(max = 150, message = "Tên không được vượt quá 150 ký tự")
    private String testimonialName;

    @NotBlank(message = "Trích dẫn là bắt buộc")
    private String testimonialQuote;

    private Boolean testimonialEnabled;

    private Integer testimonialDisplayOrder;

    private ImageDto image;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}
