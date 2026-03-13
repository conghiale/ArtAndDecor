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
 * Matches database schema: ORDER_STATE_HISTORY_ID, ORDER_ID, OLD_STATE_ID, NEW_STATE_ID, CHANGED_BY_USER_ID, CREATED_DT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStateHistoryDto {
    
    private Long historyId; // Maps to ORDER_STATE_HISTORY_ID
    private Long orderStateHistoryId; // Keep for backward compatibility
    
    // Simple field mappings for API usage
    private Long orderId;
    private String orderCode;
    private Long changedByUserId;
    private String changedByUserName;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime stateChangeDate; // Maps to CREATED_DT
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDt; // Maps to CREATED_DT
    
    // Nested related entities following clean architecture
    private OrderDto order;
    private OrderStateDto oldState; // Maps to OLD_STATE_ID
    private OrderStateDto newState; // Maps to NEW_STATE_ID
    private UserDto changedByUser; // Maps to CHANGED_BY_USER_ID
    
    // Computed fields for display
    private String transitionDescription;
    
    /**
     * Get transition description for display
     * @return Description of state change
     */
    public String getTransitionDescriptionValue() {
        if (oldState != null && newState != null) {
            return String.format("Changed from %s to %s", 
                oldState.getOrderStateName(), 
                newState.getOrderStateName());
        }
        return "State transition";
    }
    
    /**
     * Get changed by user name for display
     * @return User name or "System"
     */
    public String getChangedByUserNameValue() {
        if (changedByUser != null && changedByUser.getFullNameValue() != null) {
            return changedByUser.getFullNameValue();
        }
        return "System";
    }
}