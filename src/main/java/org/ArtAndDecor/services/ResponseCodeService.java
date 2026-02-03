package org.ArtAndDecor.services;

import org.ArtAndDecor.dto.ResponseCodeDto;
import org.ArtAndDecor.model.ResponseCode;
import org.ArtAndDecor.repository.ResponseCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for ResponseCode management
 */
@Service
public class ResponseCodeService {

    private final ResponseCodeRepository responseCodeRepository;

    @Autowired
    public ResponseCodeService(ResponseCodeRepository responseCodeRepository) {
        this.responseCodeRepository = responseCodeRepository;
    }

    /**
     * Get response code by name
     * @param codeName Response code name
     * @return ResponseCodeDto if found, null otherwise
     */
    public ResponseCodeDto getResponseCodeByName(String codeName) {
        Optional<ResponseCode> responseCode = responseCodeRepository.findEnabledResponseCodeByName(codeName);
        return responseCode.map(this::convertToDto).orElse(null);
    }

    /**
     * Get response message by code name (prioritize Vietnamese message)
     * @param codeName Response code name
     * @return Response message or default message
     */
    public String getResponseMessage(String codeName) {
        ResponseCodeDto responseCode = getResponseCodeByName(codeName);
        if (responseCode != null) {
            return responseCode.getResponseCodeRemark() != null
                ? responseCode.getResponseCodeRemark()
                : responseCode.getResponseCodeRemarkEn();
        }
        return "Mã phản hồi không xác định";
    }

    /**
     * Get English response message by code name
     * @param codeName Response code name
     * @return English response message or default message
     */
    public String getEnglishResponseMessage(String codeName) {
        ResponseCodeDto responseCode = getResponseCodeByName(codeName);
        if (responseCode != null) {
            return responseCode.getResponseCodeRemarkEn() != null
                ? responseCode.getResponseCodeRemarkEn()
                : responseCode.getResponseCodeRemark();
        }
        return "Unknown response code";
    }

    /**
     * Check if response code exists
     * @param codeName Response code name
     * @return true if exists and enabled, false otherwise
     */
    public boolean isResponseCodeExists(String codeName) {
        return responseCodeRepository.findEnabledResponseCodeByName(codeName).isPresent();
    }

    /**
     * Convert ResponseCode entity to DTO
     * @param responseCode ResponseCode entity
     * @return ResponseCodeDto
     */
    private ResponseCodeDto convertToDto(ResponseCode responseCode) {
        ResponseCodeDto dto = new ResponseCodeDto();
        dto.setResponseCodeId(responseCode.getResponseCodeId());
        dto.setResponseCodeName(responseCode.getResponseCodeName());
        dto.setResponseCodeRemarkEn(responseCode.getResponseCodeRemarkEn());
        dto.setResponseCodeRemark(responseCode.getResponseCodeRemark());
        dto.setResponseCodeEnabled(responseCode.getResponseCodeEnabled());
        dto.setCreatedDt(responseCode.getCreatedDt());
        dto.setModifiedDt(responseCode.getModifiedDt());
        return dto;
    }

    /**
     * Create or update response code
     * @param responseCodeDto ResponseCode DTO
     * @return Created/Updated ResponseCodeDto
     */
    public ResponseCodeDto createOrUpdateResponseCode(ResponseCodeDto responseCodeDto) {
        ResponseCode responseCode = convertToEntity(responseCodeDto);
        ResponseCode saved = responseCodeRepository.save(responseCode);
        return convertToDto(saved);
    }

    /**
     * Convert DTO to entity
     * @param dto ResponseCodeDto
     * @return ResponseCode entity
     */
    private ResponseCode convertToEntity(ResponseCodeDto dto) {
        ResponseCode responseCode = new ResponseCode();
        responseCode.setResponseCodeId(dto.getResponseCodeId());
        responseCode.setResponseCodeName(dto.getResponseCodeName());
        responseCode.setResponseCodeRemarkEn(dto.getResponseCodeRemarkEn());
        responseCode.setResponseCodeRemark(dto.getResponseCodeRemark());
        responseCode.setResponseCodeEnabled(dto.getResponseCodeEnabled());
        return responseCode;
    }
}