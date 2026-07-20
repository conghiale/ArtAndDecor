package org.artanddecor.dto;

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
    
    @NotBlank(message = "Tên trạng thái vận chuyển là bắt buộc")
    @Size(max = 64, message = "Tên trạng thái vận chuyển không được vượt quá 64 ký tự")
    private String shipmentStateName;
    
    @Size(max = 256, message = "Tên hiển thị trạng thái vận chuyển không được vượt quá 256 ký tự")
    private String shipmentStateDisplayName;
    
    @NotBlank(message = "Ghi chú trạng thái vận chuyển là bắt buộc")
    @Size(max = 256, message = "Ghi chú trạng thái vận chuyển không được vượt quá 256 ký tự")
    private String shipmentStateRemark;
    
    @NotNull(message = "Cờ trạng thái vận chuyển là bắt buộc")
    private Boolean shipmentStateEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDt;

}
