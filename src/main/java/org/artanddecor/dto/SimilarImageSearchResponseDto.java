package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO for AI Similar Image Search Response
 * Wraps the entire response from AI service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimilarImageSearchResponseDto {
    
    private List<SimilarImageResultDto> results;
}