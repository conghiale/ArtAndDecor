package org.artanddecor.services.impl;

import lombok.RequiredArgsConstructor;
import org.artanddecor.dto.ContactDto;
import org.artanddecor.dto.ContactRequest;
import org.artanddecor.model.Contact;
import org.artanddecor.repository.ContactRepository;
import org.artanddecor.services.ContactService;
import org.artanddecor.utils.ContactMapper;
import org.artanddecor.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        
        // Auto-generate slug if not provided
        String slug = contactRequest.getContactSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(contactRequest.getContactName());
            contactRequest.setContactSlug(slug);
            logger.info("Auto-generated slug for contact: {}", slug);
        }
        
        // Create entity using mapper
        Contact contact = contactMapper.toContactEntity(contactRequest);
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
        
        // Auto-generate slug if not provided
        String slug = contactRequest.getContactSlug();
        if (slug == null || slug.isBlank()) {
            slug = Utils.generateSlug(contactRequest.getContactName());
            contactRequest.setContactSlug(slug);
            logger.info("Auto-generated slug for contact: {}", slug);
        }
        
        // Update entity using mapper
        contactMapper.updateContactFromRequest(contact, contactRequest);
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
}
