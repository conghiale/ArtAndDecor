package org.ArtAndDecor.utils;

import org.ArtAndDecor.dto.ImageDto;
import org.ArtAndDecor.dto.SeoMetaDto;
import org.ArtAndDecor.model.Image;
import org.ArtAndDecor.model.SeoMeta;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping entities to DTOs for Image Management
 * Provides comprehensive mapping methods with different levels of detail
 * Note: ImageFormat table has been removed; imageSize field now stores format information
 */
@Component
public class ImageMapperUtil {

    // =============================================
    // IMAGE MAPPING METHODS
    // =============================================

    /**
     * Convert Image entity to ImageDto with basic information
     * @param image Image entity
     * @return ImageDto with basic fields populated
     */
    public static ImageDto toBasicDto(Image image) {
        if (image == null) return null;
        
        ImageDto dto = new ImageDto();
        dto.setImageId(image.getImageId());
        dto.setImageName(image.getImageName());
        dto.setImageDisplayName(image.getImageDisplayName());
        dto.setImageSlug(image.getImageSlug());
        dto.setImageSize(image.getImageSize());
        dto.setImageRemarkEn(image.getImageRemarkEn());
        dto.setImageRemark(image.getImageRemark());
        dto.setCreatedDt(image.getCreatedDt());
        dto.setModifiedDt(image.getModifiedDt());
        
        return dto;
    }

    /**
     * Convert Image entity to ImageDto with related entity IDs and nested DTOs
     * @param image Image entity
     * @return ImageDto with nested DTO objects
     */
    public static ImageDto toDetailedDto(Image image) {
        if (image == null) return null;
        
        ImageDto dto = toBasicDto(image);
        
        // Set nested DTO objects
        dto.setSeoMeta(toSeoMetaDto(image.getSeoMeta()));
        
        return dto;
    }

    // =============================================
    // SEO META MAPPING METHODS
    // =============================================

    /**
     * Convert SeoMeta entity to SeoMetaDto (main class - includes nested objects)
     */
    public static SeoMetaDto toSeoMetaDto(SeoMeta seoMeta) {
        if (seoMeta == null) return null;
        
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



    // =============================================
    // VALIDATION HELPER METHODS
    // =============================================

    /**
     * Check if Image entity has SEO data
     */
    public static boolean hasSeoData(Image image) {
        return image != null && image.getSeoMeta() != null;
    }
    
    // =============================================
    // HELPER METHODS TO GET FOREIGN KEY IDs
    // =============================================
    
    /**
     * Get imageFormatId from ImageDto nested object
     */
    public static Long getImageFormatId(ImageDto imageDto) {
        return imageDto != null && imageDto.getImageFormat() != null ? 
               imageDto.getImageFormat().getImageFormatId() : null;
    }
    
    /**
     * Get seoMetaId from ImageDto nested object
     */
    public static Long getSeoMetaId(ImageDto imageDto) {
        return imageDto != null && imageDto.getSeoMeta() != null ? 
               imageDto.getSeoMeta().getSeoMetaId() : null;
    }
}