package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ContactDto;
import org.artanddecor.dto.ContactRequest;
import org.artanddecor.dto.CustomerContactRequestDto;
import org.artanddecor.dto.SeoMetaDto;
import org.artanddecor.dto.SeoMetaRequestDto;
import org.artanddecor.model.Contact;
import org.artanddecor.repository.ContactRepository;
import org.artanddecor.services.ContactService;
import org.artanddecor.services.EmailService;
import org.artanddecor.services.SeoMetaService;
import org.artanddecor.utils.ContactMapper;
import org.artanddecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Contact Service Implementation
 * Implements business logic for Contact management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ContactServiceImpl implements ContactService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final SeoMetaService seoMetaService;
    private final EmailService emailService;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactDto> findContactBySlug(String contactSlug) {
        logger.debug("Finding contact by slug: {}", contactSlug);
        return contactRepository.findByContactSlug(contactSlug)
                .map(contactMapper::toContactDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactDto> findContactById(Long contactId) {
        logger.debug("Finding contact by ID: {}", contactId);
        return contactRepository.findById(contactId)
                .map(contactMapper::toContactDto);
    }
    
    @Override
    public ContactDto createContact(ContactRequest contactRequest) {
        logger.info("Creating new contact: {}", contactRequest.getContactName());
        
        // Handle SEO meta if provided
        Long seoMetaId = null;
        if (contactRequest.getSeoMeta() != null) {
            seoMetaId = handleSeoMeta(contactRequest.getSeoMeta(), null);
        }
        
        // Auto-generate slug if not provided
        String slug = contactRequest.getContactSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(contactRequest.getContactName());
            contactRequest.setContactSlug(slug);
            logger.info("Auto-generated slug for contact: {}", slug);
        }
        
        // Create entity using mapper with SEO meta ID
        Contact contact = contactMapper.toContactEntity(contactRequest, seoMetaId);
        
        Contact saved = contactRepository.save(contact);
        
        logger.info("Contact created successfully with ID: {}", saved.getContactId());
        return contactMapper.toContactDto(saved);
    }
    
    @Override
    public ContactDto updateContact(Long contactId, ContactRequest contactRequest) {
        logger.info("Updating contact with ID: {}", contactId);
        
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> {
                    logger.error("Contact not found with ID: {}", contactId);
                    return new IllegalArgumentException("Contact not found with ID: " + contactId);
                });
        
        // Handle SEO meta if provided
        Long seoMetaId = null;
        if (contactRequest.getSeoMeta() != null) {
            seoMetaId = handleSeoMeta(contactRequest.getSeoMeta(), contact.getSeoMetaId());
        } else {
            seoMetaId = contact.getSeoMetaId(); // Keep existing SEO meta
        }
        
        // Auto-generate slug if not provided
        String slug = contactRequest.getContactSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(contactRequest.getContactName());
            contactRequest.setContactSlug(slug);
            logger.info("Auto-generated slug for contact: {}", slug);
        }
        
        // Update entity using mapper with SEO meta ID
        contactMapper.updateContactFromRequest(contact, contactRequest, seoMetaId);
        
        Contact updated = contactRepository.save(contact);
        
        logger.info("Contact updated successfully with ID: {}", updated.getContactId());
        return contactMapper.toContactDto(updated);
    }
    
    @Override
    public ContactDto updateContactStatus(Long contactId, Boolean enabled) {
        logger.info("Updating contact status - ID: {}, Enabled: {}", contactId, enabled);
        
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> {
                    logger.error("Contact not found with ID: {}", contactId);
                    return new IllegalArgumentException("Contact not found with ID: " + contactId);
                });
        
        contact.setContactEnabled(enabled);
        Contact updated = contactRepository.save(contact);
        
        logger.info("Contact status updated successfully with ID: {}", contactId);
        return contactMapper.toContactDto(updated);
    }
    
    @Override
    public void deleteContact(Long contactId) {
        logger.info("Deleting contact with ID: {}", contactId);
        
        if (!contactRepository.existsById(contactId)) {
            logger.error("Contact not found with ID: {}", contactId);
            throw new IllegalArgumentException("Contact not found with ID: " + contactId);
        }
        
        contactRepository.deleteById(contactId);
        logger.info("Contact deleted successfully with ID: {}", contactId);
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public long getTotalContactCount() {
        return contactRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> findContactsByCriteria(String contactName, Boolean contactEnabled, String textSearch, Pageable pageable) {
        logger.debug("Finding contacts by criteria - name: {}, enabled: {}, textSearch: {}", 
                    contactName, contactEnabled, textSearch);
        return contactRepository.findContactsByCriteria(contactName, contactEnabled, textSearch, pageable)
                .map(contactMapper::toContactDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getAllContactNames() {
        logger.debug("Getting all contact names");
        return contactRepository.findAllContactNames();
    }
    
    @Override
    public void sendCustomerContactEmail(CustomerContactRequestDto customerContactRequest) {
        logger.info("Processing customer contact email from: {} - {}", 
                    customerContactRequest.getCustomerName(), customerContactRequest.getCustomerEmail());
                    
        try {
            // Get all enabled contacts using existing method
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE); // Get all contacts
            Page<ContactDto> enabledContacts = findContactsByCriteria(null, true, null, pageable);
            
            if (enabledContacts.isEmpty()) {
                logger.warn("No enabled contacts found to send customer contact email");
                throw new RuntimeException("Không tìm thấy địa chỉ liên hệ để gửi email");
            }
            
            // Build email content
            String subject = buildContactEmailSubject(customerContactRequest);
            String content = buildContactEmailContent(customerContactRequest);
            
            // Send email to all enabled contact addresses
            int successCount = 0;
            int totalCount = 0;
            
            for (ContactDto contact : enabledContacts.getContent()) {
                if (contact.getContactEmail() != null && !contact.getContactEmail().trim().isEmpty()) {
                    totalCount++;
                    try {
                        emailService.sendNotificationEmail(contact.getContactEmail(), subject, content);
                        successCount++;
                        logger.debug("Sent customer contact email to: {}", contact.getContactEmail());
                    } catch (Exception e) {
                        logger.error("Failed to send customer contact email to {}: {}", contact.getContactEmail(), e.getMessage());
                        // Continue sending to other contacts
                    }
                }
            }
            
            if (successCount == 0) {
                logger.error("Failed to send customer contact email to any contact addresses");
                throw new RuntimeException("Không thể gửi email liên hệ. Vui lòng thử lại sau.");
            }
            
            logger.info("Customer contact email sent successfully to {}/{} contact addresses", successCount, totalCount);
            
        } catch (RuntimeException e) {
            throw e; // Re-throw runtime exceptions
        } catch (Exception e) {
            logger.error("Unexpected error while sending customer contact email: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi hệ thống khi gửi email liên hệ", e);
        }
    }
    
    /**
     * Build email subject for customer contact
     * @param customerContactRequest Customer contact request
     * @return Email subject
     */
    private String buildContactEmailSubject(CustomerContactRequestDto customerContactRequest) {
        return String.format("[LIÊN HỆ KHÁCH HÀNG] %s - %s", 
                           customerContactRequest.getCustomerName(), 
                           customerContactRequest.getCustomerEmail());
    }
    
    /**
     * Build email content for customer contact
     * @param customerContactRequest Customer contact request
     * @return Email content
     */
    private String buildContactEmailContent(CustomerContactRequestDto customerContactRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);
        
        return String.format("""
            THÔNG TIN LIÊN HỆ KHÁCH HÀNG
            ═══════════════════════════════════════════════════════════════
            
            📋 THÔNG TIN KHÁCH HÀNG:
            • Tên khách hàng: %s
            • Email: %s
            • Số điện thoại: %s
            • Thời gian gửi: %s
            
            ✉️ NỘI DUNG TIN NHẮN:
            ───────────────────────────────────────────────────────────────
            %s
            ───────────────────────────────────────────────────────────────
            
            📞 YÊU CẦU XỬ LÝ:
            • Vui lòng liên hệ lại khách hàng trong thời gian sớm nhất
            • Gọi điện hoặc trả lời email để hỗ trợ khách hàng
            • Cập nhật trạng thái xử lý sau khi hoàn thành
            
            ⚠️ LƯU Ý:
            • Email này được gửi tự động từ hệ thống Art and Decor
            • Không trả lời trực tiếp email này
            • Liên hệ trực tiếp với khách hàng theo thông tin trên
            
            ═══════════════════════════════════════════════════════════════
            Hệ thống Art and Decor - %s
            """,
            customerContactRequest.getCustomerName(),
            customerContactRequest.getCustomerEmail(), 
            customerContactRequest.getCustomerPhone(),
            currentTime,
            customerContactRequest.getMessage(),
            currentTime
        );
    }
    
    /**
     * Handle SEO meta creation or update - Private helper method
     * Encapsulates business logic for SEO meta management within service
     * @param seoMetaRequest SEO meta request data
     * @param existingSeoMetaId Existing SEO meta ID (null if creating new)
     * @return SEO meta ID after creation/update
     * @throws IllegalArgumentException if SEO meta operation fails
     */
    private Long handleSeoMeta(SeoMetaRequestDto seoMetaRequest, Long existingSeoMetaId) {
        logger.debug("Handling SEO meta - existing ID: {}, title: {}", 
                    existingSeoMetaId, seoMetaRequest.getSeoMetaTitle());
                    
        if (existingSeoMetaId != null) {
            // Update existing SEO meta
            logger.debug("Updating existing SEO meta with ID: {}", existingSeoMetaId);
            try {
                SeoMetaDto seoMetaDto = convertSeoMetaRequestToDto(seoMetaRequest);
                seoMetaService.updateSeoMeta(existingSeoMetaId, seoMetaDto);
                logger.info("Successfully updated SEO meta ID: {}", existingSeoMetaId);
                return existingSeoMetaId;
            } catch (Exception e) {
                logger.error("Failed to update SEO meta: {}", e.getMessage(), e);
                throw new IllegalArgumentException("Failed to update SEO meta: " + e.getMessage(), e);
            }
        } else {
            // Create new SEO meta
            logger.debug("Creating new SEO meta with title: {}", seoMetaRequest.getSeoMetaTitle());
            try {
                SeoMetaDto seoMetaDto = convertSeoMetaRequestToDto(seoMetaRequest);
                SeoMetaDto createdSeoMeta = seoMetaService.createSeoMeta(seoMetaDto);
                logger.info("Successfully created SEO meta with ID: {}", createdSeoMeta.getSeoMetaId());
                return createdSeoMeta.getSeoMetaId();
            } catch (Exception e) {
                logger.error("Failed to create SEO meta: {}", e.getMessage(), e);
                throw new IllegalArgumentException("Failed to create SEO meta: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Convert SeoMetaRequestDto to SeoMetaDto
     * Private helper method for DTO conversion
     * @param seoMetaRequest SEO meta request
     * @return SeoMetaDto
     */
    private SeoMetaDto convertSeoMetaRequestToDto(SeoMetaRequestDto seoMetaRequest) {
        return SeoMetaDto.builder()
                .seoMetaTitle(seoMetaRequest.getSeoMetaTitle())
                .seoMetaDescription(seoMetaRequest.getSeoMetaDescription())
                .seoMetaKeywords(seoMetaRequest.getSeoMetaKeywords())
                .seoMetaCanonicalUrl(seoMetaRequest.getSeoMetaCanonicalUrl())
                .seoMetaIndex(seoMetaRequest.getSeoMetaIndex() != null ? seoMetaRequest.getSeoMetaIndex() : true)
                .seoMetaFollow(seoMetaRequest.getSeoMetaFollow() != null ? seoMetaRequest.getSeoMetaFollow() : true)
                .seoMetaSchemaType(seoMetaRequest.getSeoMetaSchemaType())
                .seoMetaCustomJson(seoMetaRequest.getSeoMetaCustomJson())
                .seoMetaEnabled(seoMetaRequest.getSeoMetaEnabled() != null ? seoMetaRequest.getSeoMetaEnabled() : true)
                .build();
    }
}
