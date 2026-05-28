package org.artanddecor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Request DTO for forgot-password step 1: identify the user.
 * {@code identifier} accepts either a USERNAME or an EMAIL address.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    @NotBlank(message = "Username or email is required")
    private String identifier;
}
