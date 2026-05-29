package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * Banner Request DTO for create and update operations
 * Images are referenced by their IDs in display order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {

    @NotBlank(message = "Banner title is required")
    @Size(max = 256, message = "Banner title must not exceed 256 characters")
    private String bannerTitle;

    @Size(max = 512, message = "Banner link must not exceed 512 characters")
    private String bannerLink;

    private Boolean bannerEnabled;

    private Integer bannerDisplayOrder;

    /**
     * Ordered list of image IDs to associate with this banner.
     * The index position determines BANNER_IMAGE_DISPLAY_ORDER.
     */
    private List<Long> imageIds;
}
