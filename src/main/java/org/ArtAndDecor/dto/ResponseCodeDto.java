package org.ArtAndDecor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for ResponseCode entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCodeDto {

    private Long responseCodeId;
    
    private String responseCodeName;
    
    private String responseCodeRemarkEn;
    
    private String responseCodeRemark;
    
    private Boolean responseCodeEnabled;
    
    private LocalDateTime createdDt;
    
    private LocalDateTime modifiedDt;
}