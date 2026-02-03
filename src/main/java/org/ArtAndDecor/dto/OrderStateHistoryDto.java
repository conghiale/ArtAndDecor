package org.ArtAndDecor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * OrderStateHistory DTO for API responses
 * Contains ORDER_STATE_HISTORY table data with nested related entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateHistoryDto {
    
    private Long orderStateHistoryId;
    private String remark;
    
    private Boolean orderStateHistoryEnabled;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt;
    
    // Nested related entities following clean architecture
    private OrderDto order;
    private OrderStateDto orderState;
    private UserDto changedByUser;
}