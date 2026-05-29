package org.artanddecor.utils;

import org.artanddecor.dto.ImageDto;
import org.artanddecor.dto.TestimonialDto;
import org.artanddecor.model.Image;
import org.artanddecor.model.Testimonial;
import org.springframework.stereotype.Component;

/**
 * Testimonial Mapper Utility for mapping between entities and DTOs
 */
@Component
public class TestimonialMapper {

    public TestimonialDto toTestimonialDto(Testimonial testimonial) {
        if (testimonial == null) return null;

        TestimonialDto.TestimonialDtoBuilder builder = TestimonialDto.builder()
                .testimonialId(testimonial.getTestimonialId())
                .testimonialName(testimonial.getTestimonialName())
                .testimonialQuote(testimonial.getTestimonialQuote())
                .testimonialEnabled(testimonial.getTestimonialEnabled())
                .testimonialDisplayOrder(testimonial.getTestimonialDisplayOrder())
                .createdDt(testimonial.getCreatedDt())
                .modifiedDt(testimonial.getModifiedDt());

        if (testimonial.getImage() != null) {
            Image img = testimonial.getImage();
            builder.image(ImageDto.builder()
                    .imageId(img.getImageId())
                    .imageName(img.getImageName())
                    .imageDisplayName(img.getImageDisplayName())
                    .imageSlug(img.getImageSlug())
                    .imageSize(img.getImageSize())
                    .imageFormat(img.getImageFormat())
                    .pathFile(img.getPathFile())
                    .build());
        }

        return builder.build();
    }
}
