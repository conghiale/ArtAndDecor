package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

/**
 * Customer Contact Request DTO
 * For customers to send contact information to admin/managers
 * Used by public API endpoint - no authentication required
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContactRequestDto {

    @NotBlank(message = "Tên khách hàng là bắt buộc")
    @Size(min = 2, max = 255, message = "Tên khách hàng phải từ 2 đến 255 ký tự")
    private String customerName;

    @NotBlank(message = "Email khách hàng là bắt buộc")
    @Email(message = "Định dạng email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String customerEmail;

    @NotBlank(message = "Số điện thoại khách hàng là bắt buộc")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Định dạng số điện thoại không hợp lệ")
    @Size(min = 10, max = 50, message = "Số điện thoại phải từ 10 đến 50 ký tự")
    private String customerPhone;

    @NotBlank(message = "Tin nhắn là bắt buộc")
    @Size(min = 10, max = 2000, message = "Tin nhắn phải từ 10 đến 2000 ký tự")
    private String message;
}