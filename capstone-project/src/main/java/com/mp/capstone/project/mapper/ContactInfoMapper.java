package com.mp.capstone.project.mapper;

import com.mp.capstone.project.dto.request.ContactInfoRequestDTO;
import com.mp.capstone.project.dto.response.ContactInfoResponseDTO;
import com.mp.capstone.project.entity.ContactInfo;
import org.springframework.stereotype.Component;

@Component
public class ContactInfoMapper {

    /**
     * Converts a ContactInfo entity to a ContactInfoResponseDTO.
     * The nested Patient is replaced with its TRN string.
     */
    public ContactInfoResponseDTO toResponseDTO(ContactInfo contactInfo) {
        if (contactInfo == null) return null;

        String trn = (contactInfo.getPat() != null) ? contactInfo.getPat().getTrn() : null;

        return new ContactInfoResponseDTO(
                contactInfo.getContactId(),
                contactInfo.getContact(),
                trn
        );
    }

    /**
     * Converts a ContactInfoRequestDTO to a new ContactInfo entity.
     * The Patient association must be set separately in the service layer.
     */
    public ContactInfo toEntity(ContactInfoRequestDTO dto) {
        if (dto == null) return null;

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setContact(dto.getContact());
        return contactInfo;
    }

    /**
     * Applies ContactInfoRequestDTO fields onto an existing managed entity.
     * Used for PUT (update) operations.
     */
    public void updateEntityFromDTO(ContactInfoRequestDTO dto, ContactInfo contactInfo) {
        contactInfo.setContact(dto.getContact());
    }
}
