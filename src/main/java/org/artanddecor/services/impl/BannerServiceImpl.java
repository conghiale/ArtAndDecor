package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.BannerDto;
import org.artanddecor.dto.BannerRequest;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Banner;
import org.artanddecor.model.BannerImage;
import org.artanddecor.model.Image;
import org.artanddecor.repository.BannerImageRepository;
import org.artanddecor.repository.BannerRepository;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.services.BannerService;
import org.artanddecor.utils.BannerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Banner Service Implementation
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BannerServiceImpl implements BannerService {

    private static final Logger logger = LoggerFactory.getLogger(BannerServiceImpl.class);

    private final BannerRepository bannerRepository;
    private final BannerImageRepository bannerImageRepository;
    private final ImageRepository imageRepository;
    private final BannerMapper bannerMapper;

    @Override
    public BannerDto createBanner(BannerRequest request) {
        logger.info("Creating new banner: {}", request.getBannerTitle());

        Banner banner = new Banner();
        banner.setBannerTitle(request.getBannerTitle());
        banner.setBannerLink(request.getBannerLink());
        banner.setBannerEnabled(request.getBannerEnabled() != null ? request.getBannerEnabled() : true);
        banner.setBannerDisplayOrder(request.getBannerDisplayOrder());

        Banner saved = bannerRepository.save(banner);

        attachImages(saved, request.getImageIds());

        Banner result = bannerRepository.findById(saved.getBannerId())
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found after save"));

        logger.info("Banner created successfully with ID: {}", result.getBannerId());
        return bannerMapper.toBannerDto(result);
    }

    @Override
    public BannerDto updateBanner(Long bannerId, BannerRequest request) {
        logger.info("Updating banner with ID: {}", bannerId);

        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with ID: " + bannerId));

        banner.setBannerTitle(request.getBannerTitle());
        banner.setBannerLink(request.getBannerLink());
        if (request.getBannerEnabled() != null) {
            banner.setBannerEnabled(request.getBannerEnabled());
        }
        banner.setBannerDisplayOrder(request.getBannerDisplayOrder());

        bannerRepository.save(banner);

        // Replace image associations
        bannerImageRepository.deleteByBannerId(bannerId);
        attachImages(banner, request.getImageIds());

        Banner result = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found after update"));

        logger.info("Banner updated successfully with ID: {}", bannerId);
        return bannerMapper.toBannerDto(result);
    }

    @Override
    @Transactional(readOnly = true)
    public BannerDto getBannerById(Long bannerId) {
        logger.debug("Getting banner by ID: {}", bannerId);

        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with ID: " + bannerId));

        return bannerMapper.toBannerDto(banner);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BannerDto> getBannersByCriteria(Boolean bannerEnabled, String textSearch, Pageable pageable) {
        logger.debug("Getting banners with criteria - enabled: {}, textSearch: {}", bannerEnabled, textSearch);

        return bannerRepository.findByCriteria(bannerEnabled, textSearch, pageable)
                .map(bannerMapper::toBannerDto);
    }

    // =============================================
    // PRIVATE HELPERS
    // =============================================

    private void attachImages(Banner banner, List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) return;

        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + imageId));

            BannerImage bannerImage = new BannerImage();
            bannerImage.setBanner(banner);
            bannerImage.setImage(image);
            bannerImage.setBannerImageDisplayOrder(i);
            bannerImageRepository.save(bannerImage);
        }
    }
}
