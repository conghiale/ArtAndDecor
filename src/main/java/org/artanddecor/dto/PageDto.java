package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Page DTO for API responses
 * Contains information from PAGE table with nested position and group information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
    
    private Long pageId;
    private PagePositionDto pagePosition;
    private PageGroupDto pageGroup;
    private String targetUrl;
    private String pageSlug;
    private String pageName;
    private String pageContent;
    private Boolean pageEnabled;
    private String pageDisplayName;
    private String pageRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}