package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * ShipmentState DTO for API requests and responses
 * Contains information from SHIPMENT_STATE table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentStateDto {
    
    private Long shipmentStateId;
    
    @NotBlank(message = "Shipment state name is required")
    @Size(max = 64, message = "Shipment state name must not exceed 64 characters")
    private String shipmentStateName;
    
    @Size(max = 256, message = "English remark must not exceed 256 characters")
    private String shipmentStateRemarkEn;
    
    @Size(max = 256, message = "Remark must not exceed 256 characters")
    private String shipmentStateRemark;
    
    @NotNull(message = "Shipment state enabled flag is required")
    private Boolean shipmentStateEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

}
