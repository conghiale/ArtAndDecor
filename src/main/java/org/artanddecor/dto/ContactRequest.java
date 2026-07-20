package org.artanddecor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * Contact Request DTO for Create and Update operations
 * Contains only fields that can be provided by client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {

    @NotBlank(message = "Tên liên hệ là bắt buộc")
    @Size(min = 2, max = 255, message = "Tên liên hệ phải có từ 2 đến 255 ký tự")
    private String contactName;

    @Pattern(regexp = "^[a-z0-9-]*$", message = "Slug liên hệ chỉ được chứa chữ thường, số và dấu gạch nối")
    @Size(max = 255, message = "Slug liên hệ không được vượt quá 255 ký tự")
    private String contactSlug;

    @Size(max = 500, message = "Địa chỉ liên hệ không được vượt quá 500 ký tự") 
    private String contactAddress;

    @Email(message = "Định dạng email không hợp lệ")
    @Size(max = 255, message = "Email liên hệ không được vượt quá 255 ký tự")
    private String contactEmail;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Định dạng số điện thoại không hợp lệ")
    @Size(max = 50, message = "Số điện thoại liên hệ không được vượt quá 50 ký tự")
    private String contactPhone;

    @Size(max = 255, message = "Fanpage liên hệ không được vượt quá 255 ký tự")
    private String contactFanpage;

    @NotNull(message = "Trạng thái bật liên hệ là bắt buộc")
    private Boolean contactEnabled;

    @Size(max = 1000, message = "Ghi chú liên hệ không được vượt quá 1000 ký tự")
    private String contactRemark;

    @Valid
    private SeoMetaRequestDto seoMeta;
}