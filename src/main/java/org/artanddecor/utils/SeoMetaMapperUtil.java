package org.artanddecor.utils;

import org.artanddecor.dto.SeoMetaDto; 
import org.artanddecor.dto.SeoMetaRequestDto;
import org.artanddecor.model.SeoMeta;

/**
 * SeoMeta Mapper Utility
 * Handles conversion between SeoMeta entities and DTOs
 */
public class SeoMetaMapperUtil {

    private SeoMetaMapperUtil() {
        // Utility class
    }

    /**
     * Convert SeoMeta entity to SeoMetaDto
     * @param seoMeta SeoMeta entity
     * @return SeoMetaDto
     */
    public static SeoMetaDto toSeoMetaDto(SeoMeta seoMeta) {
        if (seoMeta == null) {
            return null;
        }
        
        return SeoMetaDto.builder()
                .seoMetaId(seoMeta.getSeoMetaId())
                .seoMetaTitle(seoMeta.getSeoMetaTitle())
                .seoMetaDescription(seoMeta.getSeoMetaDescription())
                .seoMetaKeywords(seoMeta.getSeoMetaKeywords())
                .seoMetaIndex(seoMeta.getSeoMetaIndex())
                .seoMetaFollow(seoMeta.getSeoMetaFollow())
                .seoMetaCanonicalUrl(seoMeta.getSeoMetaCanonicalUrl())
                .seoMetaImageName(seoMeta.getSeoMetaImageName())
                .seoMetaSchemaType(seoMeta.getSeoMetaSchemaType())
                .seoMetaCustomJson(seoMeta.getSeoMetaCustomJson())
                .seoMetaEnabled(seoMeta.getSeoMetaEnabled())
                .createdDt(seoMeta.getCreatedDt())
                .modifiedDt(seoMeta.getModifiedDt())
                .build();
    }

    /**
     * Convert SeoMetaDto to SeoMeta entity for creation
     * @param seoMetaDto SeoMetaDto
     * @return SeoMeta entity
     */
    public static SeoMeta toSeoMetaEntity(SeoMetaDto seoMetaDto) {
        if (seoMetaDto == null) {
            return null;
        }
        
        SeoMeta seoMeta = new SeoMeta();
        seoMeta.setSeoMetaTitle(seoMetaDto.getSeoMetaTitle());
        seoMeta.setSeoMetaDescription(seoMetaDto.getSeoMetaDescription());
        seoMeta.setSeoMetaKeywords(seoMetaDto.getSeoMetaKeywords());
        seoMeta.setSeoMetaIndex(seoMetaDto.getSeoMetaIndex());
        seoMeta.setSeoMetaFollow(seoMetaDto.getSeoMetaFollow());
        seoMeta.setSeoMetaCanonicalUrl(seoMetaDto.getSeoMetaCanonicalUrl());
        seoMeta.setSeoMetaImageName(seoMetaDto.getSeoMetaImageName());
        seoMeta.setSeoMetaSchemaType(seoMetaDto.getSeoMetaSchemaType());
        seoMeta.setSeoMetaCustomJson(seoMetaDto.getSeoMetaCustomJson());
        seoMeta.setSeoMetaEnabled(seoMetaDto.getSeoMetaEnabled());
        
        return seoMeta;
    }

    /**
     * Update existing SeoMeta entity with data from SeoMetaDto
     * @param existingSeoMeta Existing SeoMeta entity to update
     * @param seoMetaDto SeoMetaDto containing new data
     */
    public static void updateSeoMetaEntity(SeoMeta existingSeoMeta, SeoMetaDto seoMetaDto) {
        if (existingSeoMeta == null || seoMetaDto == null) {
            return;
        }
        
        existingSeoMeta.setSeoMetaTitle(seoMetaDto.getSeoMetaTitle());
        existingSeoMeta.setSeoMetaDescription(seoMetaDto.getSeoMetaDescription());
        existingSeoMeta.setSeoMetaKeywords(seoMetaDto.getSeoMetaKeywords());
        existingSeoMeta.setSeoMetaIndex(seoMetaDto.getSeoMetaIndex());
        existingSeoMeta.setSeoMetaFollow(seoMetaDto.getSeoMetaFollow());
        existingSeoMeta.setSeoMetaCanonicalUrl(seoMetaDto.getSeoMetaCanonicalUrl());
        existingSeoMeta.setSeoMetaImageName(seoMetaDto.getSeoMetaImageName());
        existingSeoMeta.setSeoMetaSchemaType(seoMetaDto.getSeoMetaSchemaType());
        existingSeoMeta.setSeoMetaCustomJson(seoMetaDto.getSeoMetaCustomJson());
        existingSeoMeta.setSeoMetaEnabled(seoMetaDto.getSeoMetaEnabled());
    }

    /**
     * Convert SeoMetaRequestDto to SeoMeta entity for creation
     * @param seoMetaRequestDto SeoMetaRequestDto
     * @return SeoMeta entity
     */
    public static SeoMeta toSeoMetaEntityFromRequest(SeoMetaRequestDto seoMetaRequestDto) {
        if (seoMetaRequestDto == null) {
            return null;
        }
        
        SeoMeta seoMeta = new SeoMeta();
        seoMeta.setSeoMetaTitle(seoMetaRequestDto.getSeoMetaTitle());
        seoMeta.setSeoMetaDescription(seoMetaRequestDto.getSeoMetaDescription());
        seoMeta.setSeoMetaKeywords(seoMetaRequestDto.getSeoMetaKeywords());
        seoMeta.setSeoMetaIndex(seoMetaRequestDto.getSeoMetaIndex() != null ? seoMetaRequestDto.getSeoMetaIndex() : true);
        seoMeta.setSeoMetaFollow(seoMetaRequestDto.getSeoMetaFollow() != null ? seoMetaRequestDto.getSeoMetaFollow() : true);
        seoMeta.setSeoMetaCanonicalUrl(seoMetaRequestDto.getSeoMetaCanonicalUrl());
        seoMeta.setSeoMetaImageName(null); // SeoMetaRequestDto doesn't have seoMetaImageName field
        seoMeta.setSeoMetaSchemaType(seoMetaRequestDto.getSeoMetaSchemaType());
        seoMeta.setSeoMetaCustomJson(seoMetaRequestDto.getSeoMetaCustomJson());
        seoMeta.setSeoMetaEnabled(seoMetaRequestDto.getSeoMetaEnabled() != null ? seoMetaRequestDto.getSeoMetaEnabled() : true);
        
        return seoMeta;
    }
}