package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PagePosition DTO for API responses
 * Contains information from PAGE_POSITION table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagePositionDto {
    
    private Long pagePositionId;
    private String pagePositionSlug;
    private String pagePositionName;
    private Boolean pagePositionEnabled;
    private String pagePositionDisplayName;
    private String pagePositionRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}