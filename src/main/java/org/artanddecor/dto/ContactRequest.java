package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * Contact Request DTO for Create and Update operations
 * Contains only fields that can be provided by client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {

    @NotBlank(message = "Contact name is required")
    @Size(min = 2, max = 255, message = "Contact name must be between 2 and 255 characters")
    private String contactName;

    @Pattern(regexp = "^[a-z0-9-]*$", message = "Contact slug must contain only lowercase letters, numbers, and hyphens")
    @Size(max = 255, message = "Contact slug must not exceed 255 characters")
    private String contactSlug;

    @Size(max = 500, message = "Contact address must not exceed 500 characters") 
    private String contactAddress;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Contact email must not exceed 255 characters")
    private String contactEmail;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Invalid phone number format")
    @Size(max = 50, message = "Contact phone must not exceed 50 characters")
    private String contactPhone;

    @Size(max = 255, message = "Contact fanpage must not exceed 255 characters")
    private String contactFanpage;

    @NotNull(message = "Contact enabled status is required")
    private Boolean contactEnabled;

    @Size(max = 1000, message = "Contact remark must not exceed 1000 characters")
    private String contactRemark;

    @Valid
    private SeoMetaRequestDto seoMeta;
}