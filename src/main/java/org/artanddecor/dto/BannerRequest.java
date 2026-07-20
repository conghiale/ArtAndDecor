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

    @NotBlank(message = "Tiêu đề banner là bắt buộc")
    @Size(max = 256, message = "Tiêu đề banner không được vượt quá 256 ký tự")
    private String bannerTitle;

    @Size(max = 512, message = "Liên kết banner không được vượt quá 512 ký tự")
    private String bannerLink;

    private Boolean bannerEnabled;

    private Integer bannerDisplayOrder;

    /**
     * Ordered list of image IDs to associate with this banner.
     * The index position determines BANNER_IMAGE_DISPLAY_ORDER.
     */
    private List<Long> imageIds;
}
