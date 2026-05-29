package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Banner DTO for API responses
 * Contains full banner information including ordered list of images
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDto {

    private Long bannerId;

    private String bannerTitle;

    private String bannerLink;

    private Boolean bannerEnabled;

    private Integer bannerDisplayOrder;

    private List<ImageDto> images;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}
