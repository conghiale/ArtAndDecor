package org.artanddecor.utils;

import org.artanddecor.dto.BannerDto;
import org.artanddecor.dto.ImageDto;
import org.artanddecor.model.Banner;
import org.artanddecor.model.BannerImage;
import org.artanddecor.model.Image;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Banner Mapper Utility for mapping between entities and DTOs
 */
@Component
public class BannerMapper {

    public BannerDto toBannerDto(Banner banner) {
        if (banner == null) return null;

        List<ImageDto> images = Collections.emptyList();
        if (banner.getBannerImages() != null && !banner.getBannerImages().isEmpty()) {
            images = banner.getBannerImages().stream()
                    .map(bi -> toImageDto(bi.getImage()))
                    .collect(Collectors.toList());
        }

        return BannerDto.builder()
                .bannerId(banner.getBannerId())
                .bannerTitle(banner.getBannerTitle())
                .bannerLink(banner.getBannerLink())
                .bannerEnabled(banner.getBannerEnabled())
                .bannerDisplayOrder(banner.getBannerDisplayOrder())
                .images(images)
                .createdDt(banner.getCreatedDt())
                .modifiedDt(banner.getModifiedDt())
                .build();
    }

    private ImageDto toImageDto(Image image) {
        if (image == null) return null;
        return ImageDto.builder()
                .imageId(image.getImageId())
                .imageName(image.getImageName())
                .imageDisplayName(image.getImageDisplayName())
                .imageSlug(image.getImageSlug())
                .imageSize(image.getImageSize())
                .imageFormat(image.getImageFormat())
                .pathFile(image.getPathFile())
                .build();
    }
}
