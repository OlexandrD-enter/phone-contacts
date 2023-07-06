package com.internship.contacts.utils.mapper;

import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.model.Contact;
import com.internship.contacts.model.Email;
import com.internship.contacts.model.PhoneNumber;

import java.util.stream.Collectors;

public class ContactMapper {
    public static ContactDTO toDTO(Contact contact) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName(contact.getName());
        contactDTO.setEmails(contact.getEmails().stream().map(Email::getEmail).collect(Collectors.toList()));
        contactDTO.setPhoneNumbers(contact.getPhoneNumbers().stream().map(PhoneNumber::getPhoneNumber).collect(Collectors.toList()));
        return contactDTO;
    }

    public static Contact toEntity(ContactDTO contactDTO) {
        Contact contact = new Contact();
        contact.setName(contactDTO.getName());
        return contact;
    }
}

