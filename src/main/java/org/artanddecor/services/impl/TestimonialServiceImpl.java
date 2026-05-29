package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.TestimonialDto;
import org.artanddecor.dto.TestimonialRequest;
import org.artanddecor.exception.ResourceNotFoundException;
import org.artanddecor.model.Image;
import org.artanddecor.model.Testimonial;
import org.artanddecor.repository.ImageRepository;
import org.artanddecor.repository.TestimonialRepository;
import org.artanddecor.services.TestimonialService;
import org.artanddecor.utils.TestimonialMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Testimonial Service Implementation
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TestimonialServiceImpl implements TestimonialService {

    private static final Logger logger = LoggerFactory.getLogger(TestimonialServiceImpl.class);

    private final TestimonialRepository testimonialRepository;
    private final ImageRepository imageRepository;
    private final TestimonialMapper testimonialMapper;

    @Override
    public TestimonialDto createTestimonial(TestimonialRequest request) {
        logger.info("Creating new testimonial: {}", request.getTestimonialName());

        Testimonial testimonial = new Testimonial();
        testimonial.setTestimonialName(request.getTestimonialName());
        testimonial.setTestimonialQuote(request.getTestimonialQuote());
        testimonial.setTestimonialEnabled(request.getTestimonialEnabled() != null ? request.getTestimonialEnabled() : true);
        testimonial.setTestimonialDisplayOrder(request.getTestimonialDisplayOrder());

        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + request.getImageId()));
            testimonial.setImage(image);
        }

        Testimonial saved = testimonialRepository.save(testimonial);
        logger.info("Testimonial created successfully with ID: {}", saved.getTestimonialId());
        return testimonialMapper.toTestimonialDto(saved);
    }

    @Override
    public TestimonialDto updateTestimonial(Long testimonialId, TestimonialRequest request) {
        logger.info("Updating testimonial with ID: {}", testimonialId);

        Testimonial testimonial = testimonialRepository.findById(testimonialId)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with ID: " + testimonialId));

        testimonial.setTestimonialName(request.getTestimonialName());
        testimonial.setTestimonialQuote(request.getTestimonialQuote());
        if (request.getTestimonialEnabled() != null) {
            testimonial.setTestimonialEnabled(request.getTestimonialEnabled());
        }
        testimonial.setTestimonialDisplayOrder(request.getTestimonialDisplayOrder());

        if (request.getImageId() != null) {
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + request.getImageId()));
            testimonial.setImage(image);
        } else {
            testimonial.setImage(null);
        }

        Testimonial updated = testimonialRepository.save(testimonial);
        logger.info("Testimonial updated successfully with ID: {}", updated.getTestimonialId());
        return testimonialMapper.toTestimonialDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TestimonialDto getTestimonialById(Long testimonialId) {
        logger.debug("Getting testimonial by ID: {}", testimonialId);

        Testimonial testimonial = testimonialRepository.findById(testimonialId)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with ID: " + testimonialId));

        return testimonialMapper.toTestimonialDto(testimonial);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TestimonialDto> getTestimonialsByCriteria(Boolean testimonialEnabled, String textSearch, Pageable pageable) {
        logger.debug("Getting testimonials with criteria - enabled: {}, textSearch: {}", testimonialEnabled, textSearch);

        return testimonialRepository.findByCriteria(testimonialEnabled, textSearch, pageable)
                .map(testimonialMapper::toTestimonialDto);
    }
}
