package org.artanddecor.utils;

import org.artanddecor.dto.ContactDto;
import org.artanddecor.dto.ContactRequest;
import org.artanddecor.model.Contact;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Contact Mapper Utility for mapping between entities and DTOs
 */
@Component
public class ContactMapper {

    /**
     * Map Contact entity to ContactDto
     */
    public ContactDto toContactDto(Contact contact) {
        if (contact == null) return null;

        return ContactDto.builder()
                .contactId(contact.getContactId())
                .contactName(contact.getContactName())
                .contactSlug(contact.getContactSlug())
                .contactAddress(contact.getContactAddress())
                .contactEmail(contact.getContactEmail())
                .contactPhone(contact.getContactPhone())
                .contactFanpage(contact.getContactFanpage())
                .contactEnabled(contact.getContactEnabled())
                .contactRemark(contact.getContactRemark())
                .seoMetaId(contact.getSeoMetaId())
                .createdDt(contact.getCreatedDt())
                .modifiedDt(contact.getModifiedDt())
                .build();
    }

    /**
     * Map ContactRequest to new Contact entity (for create operations)
     */
    public Contact toContactEntity(ContactRequest request) {
        if (request == null) return null;

        Contact entity = new Contact();
        entity.setContactName(request.getContactName());
        entity.setContactSlug(request.getContactSlug());
        entity.setContactAddress(request.getContactAddress());
        entity.setContactEmail(request.getContactEmail());
        entity.setContactPhone(request.getContactPhone());
        entity.setContactFanpage(request.getContactFanpage());
        entity.setContactEnabled(request.getContactEnabled() != null ? request.getContactEnabled() : true);
        entity.setContactRemark(request.getContactRemark());
        entity.setSeoMetaId(request.getSeoMetaId());

        // Auto-fill timestamps
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDt(now);
        entity.setModifiedDt(now);

        return entity;
    }

    /**
     * Update Contact entity from ContactRequest (for update operations)
     */
    public void updateContactFromRequest(Contact entity, ContactRequest request) {
        if (entity == null || request == null) return;

        entity.setContactName(request.getContactName());
        entity.setContactSlug(request.getContactSlug());
        entity.setContactAddress(request.getContactAddress());
        entity.setContactEmail(request.getContactEmail());
        entity.setContactPhone(request.getContactPhone());
        entity.setContactFanpage(request.getContactFanpage());
        if (request.getContactEnabled() != null) {
            entity.setContactEnabled(request.getContactEnabled());
        }
        entity.setContactRemark(request.getContactRemark());
        if (request.getSeoMetaId() != null) {
            entity.setSeoMetaId(request.getSeoMetaId());
        }

        // Auto-update modification timestamp
        entity.setModifiedDt(LocalDateTime.now());
    }
}