package org.artanddecor.utils;

import org.artanddecor.dto.*;
import org.artanddecor.model.*;
import org.springframework.stereotype.Component;

/**
 * Blog Mapper Utility for mapping between entities and DTOs
 */
@Component
public class BlogMapper {

    // ===== BLOG TYPE MAPPING =====

    public BlogTypeDto toBlogTypeDto(BlogType blogType) {
        if (blogType == null) return null;

        BlogTypeDto dto = new BlogTypeDto();
        dto.setBlogTypeId(blogType.getBlogTypeId());
        dto.setBlogTypeSlug(blogType.getBlogTypeSlug());
        dto.setBlogTypeName(blogType.getBlogTypeName());
        dto.setBlogTypeDisplayName(blogType.getBlogTypeDisplayName());
        dto.setBlogTypeRemark(blogType.getBlogTypeRemark());
        dto.setBlogTypeEnabled(blogType.getBlogTypeEnabled());
        dto.setCreatedDt(blogType.getCreatedDt());
        dto.setModifiedDt(blogType.getModifiedDt());

        // Map related entities
        if (blogType.getImage() != null) {
            dto.setImage(toImageDto(blogType.getImage()));
        }
        if (blogType.getSeoMeta() != null) {
            dto.setSeoMeta(toSeoMetaDto(blogType.getSeoMeta()));
        }

        return dto;
    }

    public BlogType toBlogTypeEntity(BlogTypeDto dto) {
        if (dto == null) return null;

        BlogType entity = new BlogType();
        entity.setBlogTypeId(dto.getBlogTypeId());
        entity.setBlogTypeSlug(dto.getBlogTypeSlug());
        entity.setBlogTypeName(dto.getBlogTypeName());
        entity.setBlogTypeDisplayName(dto.getBlogTypeDisplayName());
        entity.setBlogTypeRemark(dto.getBlogTypeRemark());
        entity.setBlogTypeEnabled(dto.getBlogTypeEnabled());

        return entity;
    }

    // ===== BLOG TYPE COMMON REQUEST MAPPING =====

    public BlogType toBlogTypeEntity(BlogTypeRequest request) {
        if (request == null) return null;

        BlogType entity = new BlogType();
        entity.setBlogTypeSlug(request.getBlogTypeSlug());
        entity.setBlogTypeName(request.getBlogTypeName());
        entity.setBlogTypeDisplayName(request.getBlogTypeDisplayName());
        entity.setBlogTypeRemark(request.getBlogTypeRemark());
        entity.setBlogTypeEnabled(request.getBlogTypeEnabled());

        return entity;
    }

    public void updateBlogTypeFromRequest(BlogType entity, BlogTypeRequest request) {
        if (request == null || entity == null) return;

        entity.setBlogTypeSlug(request.getBlogTypeSlug());
        entity.setBlogTypeName(request.getBlogTypeName());
        entity.setBlogTypeDisplayName(request.getBlogTypeDisplayName());
        entity.setBlogTypeRemark(request.getBlogTypeRemark());
        entity.setBlogTypeEnabled(request.getBlogTypeEnabled());
    }

    // ===== BLOG CATEGORY MAPPING =====

    public BlogCategoryDto toBlogCategoryDto(BlogCategory blogCategory) {
        if (blogCategory == null) return null;

        BlogCategoryDto dto = new BlogCategoryDto();
        dto.setBlogCategoryId(blogCategory.getBlogCategoryId());
        dto.setBlogCategorySlug(blogCategory.getBlogCategorySlug());
        dto.setBlogCategoryName(blogCategory.getBlogCategoryName());
        dto.setBlogCategoryDisplayName(blogCategory.getBlogCategoryDisplayName());
        dto.setBlogCategoryRemark(blogCategory.getBlogCategoryRemark());
        dto.setBlogCategoryEnabled(blogCategory.getBlogCategoryEnabled());
        dto.setCreatedDt(blogCategory.getCreatedDt());
        dto.setModifiedDt(blogCategory.getModifiedDt());

        // Map related entities
        if (blogCategory.getBlogType() != null) {
            dto.setBlogType(toBlogTypeDto(blogCategory.getBlogType()));
        }
        if (blogCategory.getImage() != null) {
            dto.setImage(toImageDto(blogCategory.getImage()));
        }
        if (blogCategory.getSeoMeta() != null) {
            dto.setSeoMeta(toSeoMetaDto(blogCategory.getSeoMeta()));
        }

        return dto;
    }

    public BlogCategory toBlogCategoryEntity(BlogCategoryDto dto) {
        if (dto == null) return null;

        BlogCategory entity = new BlogCategory();
        entity.setBlogCategoryId(dto.getBlogCategoryId());
        entity.setBlogCategorySlug(dto.getBlogCategorySlug());
        entity.setBlogCategoryName(dto.getBlogCategoryName());
        entity.setBlogCategoryDisplayName(dto.getBlogCategoryDisplayName());
        entity.setBlogCategoryRemark(dto.getBlogCategoryRemark());
        entity.setBlogCategoryEnabled(dto.getBlogCategoryEnabled());

        return entity;
    }

    // ===== BLOG CATEGORY COMMON REQUEST MAPPING =====

    public BlogCategory toBlogCategoryEntity(BlogCategoryRequest request) {
        if (request == null) return null;

        BlogCategory entity = new BlogCategory();
        entity.setBlogCategorySlug(request.getBlogCategorySlug());
        entity.setBlogCategoryName(request.getBlogCategoryName());
        entity.setBlogCategoryDisplayName(request.getBlogCategoryDisplayName());
        entity.setBlogCategoryRemark(request.getBlogCategoryRemark());
        entity.setBlogCategoryEnabled(request.getBlogCategoryEnabled());

        return entity;
    }

    public void updateBlogCategoryFromRequest(BlogCategory entity, BlogCategoryRequest request) {
        if (request == null || entity == null) return;

        entity.setBlogCategorySlug(request.getBlogCategorySlug());
        entity.setBlogCategoryName(request.getBlogCategoryName());
        entity.setBlogCategoryDisplayName(request.getBlogCategoryDisplayName());
        entity.setBlogCategoryRemark(request.getBlogCategoryRemark());
        entity.setBlogCategoryEnabled(request.getBlogCategoryEnabled());
    }

    // ===== BLOG MAPPING =====

    public BlogDto toBlogDto(Blog blog) {
        if (blog == null) return null;

        BlogDto dto = new BlogDto();
        dto.setBlogId(blog.getBlogId());
        dto.setBlogTitle(blog.getBlogTitle());
        dto.setBlogSlug(blog.getBlogSlug());
        dto.setBlogContent(blog.getBlogContent());
        dto.setBlogEnabled(blog.getBlogEnabled());
        dto.setBlogRemark(blog.getBlogRemark());
        dto.setCreatedDt(blog.getCreatedDt());
        dto.setModifiedDt(blog.getModifiedDt());

        // Map related entities
        if (blog.getBlogCategory() != null) {
            dto.setBlogCategory(toBlogCategoryDto(blog.getBlogCategory()));
        }
        if (blog.getSeoMeta() != null) {
            dto.setSeoMeta(toSeoMetaDto(blog.getSeoMeta()));
        }
        if (blog.getImage() != null) {
            dto.setImage(toImageDto(blog.getImage()));
        }

        return dto;
    }

    public Blog toBlogEntity(BlogDto dto) {
        if (dto == null) return null;

        Blog entity = new Blog();
        entity.setBlogId(dto.getBlogId());
        entity.setBlogTitle(dto.getBlogTitle());
        entity.setBlogSlug(dto.getBlogSlug());
        entity.setBlogContent(dto.getBlogContent());
        entity.setBlogEnabled(dto.getBlogEnabled());
        entity.setBlogRemark(dto.getBlogRemark());

        return entity;
    }

    // ===== BLOG COMMON REQUEST MAPPING =====

    public Blog toBlogEntity(BlogRequest request) {
        if (request == null) return null;

        Blog entity = new Blog();
        entity.setBlogTitle(request.getBlogTitle());
        entity.setBlogSlug(request.getBlogSlug());
        entity.setBlogContent(request.getBlogContent());
        entity.setBlogEnabled(request.getBlogEnabled());
        entity.setBlogRemark(request.getBlogRemark());

        return entity;
    }

    public void updateBlogFromRequest(Blog entity, BlogRequest request) {
        if (request == null || entity == null) return;

        entity.setBlogTitle(request.getBlogTitle());
        entity.setBlogSlug(request.getBlogSlug());
        entity.setBlogContent(request.getBlogContent());
        entity.setBlogEnabled(request.getBlogEnabled());
        entity.setBlogRemark(request.getBlogRemark());
    }

    // ===== HELPER METHODS FOR RELATED ENTITIES =====

    private ImageDto toImageDto(Image image) {
        if (image == null) return null;
        // Simplified mapping - expand as needed
        ImageDto dto = new ImageDto();
        dto.setImageId(image.getImageId());
        dto.setImageName(image.getImageName());
        dto.setImageDisplayName(image.getImageDisplayName());
        dto.setImageSlug(image.getImageSlug());
        dto.setImageSize(image.getImageSize());
        dto.setImageFormat(image.getImageFormat());
        dto.setPathFile(image.getPathFile());
        dto.setImageRemark(image.getImageRemark());
        dto.setCreatedDt(image.getCreatedDt());
        dto.setModifiedDt(image.getModifiedDt());
        return dto;
    }

    private SeoMetaDto toSeoMetaDto(SeoMeta seoMeta) {
        if (seoMeta == null) return null;
        // Simplified mapping - expand as needed
        SeoMetaDto dto = new SeoMetaDto();
        dto.setSeoMetaId(seoMeta.getSeoMetaId());
        dto.setSeoMetaTitle(seoMeta.getSeoMetaTitle());
        dto.setSeoMetaDescription(seoMeta.getSeoMetaDescription());
        dto.setSeoMetaKeywords(seoMeta.getSeoMetaKeywords());
        dto.setSeoMetaIndex(seoMeta.getSeoMetaIndex());
        dto.setSeoMetaFollow(seoMeta.getSeoMetaFollow());
        dto.setSeoMetaCanonicalUrl(seoMeta.getSeoMetaCanonicalUrl());
        dto.setSeoMetaImageName(seoMeta.getSeoMetaImageName());
        dto.setSeoMetaSchemaType(seoMeta.getSeoMetaSchemaType());
        dto.setSeoMetaCustomJson(seoMeta.getSeoMetaCustomJson());
        dto.setSeoMetaEnabled(seoMeta.getSeoMetaEnabled());
        dto.setCreatedDt(seoMeta.getCreatedDt());
        dto.setModifiedDt(seoMeta.getModifiedDt());
        return dto;
    }
}