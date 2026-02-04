package org.ArtAndDecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.ArtAndDecor.dto.ContactDto;
import org.ArtAndDecor.model.Contact;
import org.ArtAndDecor.repository.ContactRepository;
import org.ArtAndDecor.services.ContactService;
import org.ArtAndDecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactDto> findContactBySlug(String contactSlug) {
        logger.debug("Finding contact by slug: {}", contactSlug);
        return contactRepository.findByContactSlug(contactSlug)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactDto> findContactById(Long contactId) {
        logger.debug("Finding contact by ID: {}", contactId);
        return contactRepository.findById(contactId)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactDto> findContactByName(String contactName) {
        logger.debug("Finding contact by name: {}", contactName);
        return contactRepository.findByContactName(contactName)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactDto> findContactByEmail(String contactEmail) {
        logger.debug("Finding contact by email: {}", contactEmail);
        return contactRepository.findByContactEmail(contactEmail)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> findAllEnabledContacts() {
        logger.debug("Finding all enabled contacts");
        return contactRepository.findByContactEnabledTrue()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> searchContactsByName(String namePattern) {
        logger.debug("Searching contacts by name pattern: {}", namePattern);
        if (namePattern == null || namePattern.isBlank()) {
            return List.of();
        }
        return contactRepository.findByContactNameContainingIgnoreCase(namePattern)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ContactDto createContact(ContactDto contactDto) {
        logger.info("Creating new contact: {}", contactDto.getContactName());
        
        // Auto-generate slug if not provided
        String slug = contactDto.getContactSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(contactDto.getContactName());
            logger.info("Auto-generated slug for contact: {}", slug);
        }
        
        // Create entity
        Contact contact = new Contact();
        contact.setContactName(contactDto.getContactName());
        contact.setContactSlug(slug);
        contact.setContactAddress(contactDto.getContactAddress());
        contact.setContactEmail(contactDto.getContactEmail());
        contact.setContactPhone(contactDto.getContactPhone());
        contact.setContactFanpage(contactDto.getContactFanpage());
        contact.setContactRemarkEn(contactDto.getContactRemarkEn());
        contact.setContactRemark(contactDto.getContactRemark());
        contact.setContactEnabled(contactDto.getContactEnabled() != null ? contactDto.getContactEnabled() : true);
        contact.setSeoMetaId(contactDto.getSeoMetaId());
        
        Contact saved = contactRepository.save(contact);
        logger.info("Contact created successfully with ID: {}", saved.getContactId());
        
        return mapToDto(saved);
    }
    
    @Override
    public ContactDto updateContact(Long contactId, ContactDto contactDto) {
        logger.info("Updating contact with ID: {}", contactId);
        
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> {
                    logger.error("Contact not found with ID: {}", contactId);
                    return new IllegalArgumentException("Contact not found with ID: " + contactId);
                });
        
        // Auto-generate slug if not provided
        String slug = contactDto.getContactSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(contactDto.getContactName());
            logger.info("Auto-generated slug for contact: {}", slug);
        }
        
        // Update entity
        contact.setContactName(contactDto.getContactName());
        contact.setContactSlug(slug);
        contact.setContactAddress(contactDto.getContactAddress());
        contact.setContactEmail(contactDto.getContactEmail());
        contact.setContactPhone(contactDto.getContactPhone());
        contact.setContactFanpage(contactDto.getContactFanpage());
        contact.setContactRemarkEn(contactDto.getContactRemarkEn());
        contact.setContactRemark(contactDto.getContactRemark());
        if (contactDto.getContactEnabled() != null) {
            contact.setContactEnabled(contactDto.getContactEnabled());
        }
        if (contactDto.getSeoMetaId() != null) {
            contact.setSeoMetaId(contactDto.getSeoMetaId());
        }
        
        Contact updated = contactRepository.save(contact);
        logger.info("Contact updated successfully with ID: {}", updated.getContactId());
        
        return mapToDto(updated);
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
        return mapToDto(updated);
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
    
    // =============================================
    // HELPER METHODS
    // =============================================

    
    /**
     * Map Contact entity to ContactDto
     * @param contact the contact entity
     * @return ContactDto
     */
    private ContactDto mapToDto(Contact contact) {
        if (contact == null) {
            return null;
        }
        
        return ContactDto.builder()
                .contactId(contact.getContactId())
                .contactName(contact.getContactName())
                .contactSlug(contact.getContactSlug())
                .contactAddress(contact.getContactAddress())
                .contactEmail(contact.getContactEmail())
                .contactPhone(contact.getContactPhone())
                .contactFanpage(contact.getContactFanpage())
                .contactEnabled(contact.getContactEnabled())
                .contactRemarkEn(contact.getContactRemarkEn())
                .contactRemark(contact.getContactRemark())
                .seoMetaId(contact.getSeoMetaId())
                .createdDt(contact.getCreatedDt())
                .modifiedDt(contact.getModifiedDt())
                .build();
    }
}
