package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI Similar Image Search Result
 * Maps the response from AI service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarImageResultDto {
    
    private Long id;
    
    private String imageUrl;
    
    private Double similarityPercent;
}