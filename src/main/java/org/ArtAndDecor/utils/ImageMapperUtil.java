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
        
        // Note: IMAGE table does not have direct relationship with SEO_META
        // SeoMeta is handled separately by entities that reference both IMAGE and SEO_META
        
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
     * Check if Image entity is valid (has basic required fields)
     */
    public static boolean isValidImage(Image image) {
        return image != null && image.getImageName() != null && image.getImageSlug() != null;
    }
    
    // =============================================
    // HELPER METHODS TO GET FOREIGN KEY IDs
    // =============================================
    
    /**
     * Get image format from ImageDto (stored as string in IMAGE_FORMAT field)
     */
    public static String getImageFormat(ImageDto imageDto) {
        return imageDto != null ? imageDto.getImageFormat() : null;
    }
}