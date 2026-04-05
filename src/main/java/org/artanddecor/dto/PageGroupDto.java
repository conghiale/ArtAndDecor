package org.artanddecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * PageGroup DTO for API responses
 * Contains information from PAGE_GROUP table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageGroupDto {
    
    private Long pageGroupId;
    private String pageGroupSlug;
    private String pageGroupName;
    private Boolean pageGroupEnabled;
    private String pageGroupDisplayName;
    private String pageGroupRemark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;
}