package com.internship.contacts.utils.mapper;

import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.model.Contact;
import com.internship.contacts.model.Email;
import com.internship.contacts.model.PhoneNumber;
import com.internship.contacts.model.User;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ContactMapper {

    public static ContactDTO toDTO(Contact contact) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName(contact.getName());
        contactDTO.setEmails(contact.getEmails().stream().map(Email::getEmail).collect(Collectors.toList()));
        contactDTO.setPhoneNumbers(contact.getPhoneNumbers().stream().map(PhoneNumber::getPhoneNumber).collect(Collectors.toList()));
        return contactDTO;
    }

    public static Contact toEntity(ContactDTO contactDTO, User user) {
        Contact contact = new Contact();
        contact.setName(contactDTO.getName());
        contact.setUser(user);
        ListsEmailsAndPhonesToStringNames(contactDTO, contact);
        return contact;
    }

    public static void ListsEmailsAndPhonesToStringNames(ContactDTO contactDTO, Contact contact) {
        List<Email> emails = contactDTO.getEmails().stream()
                .map(email -> {
                    Email emailObj = new Email();
                    emailObj.setEmail(email);
                    emailObj.setContact(contact);
                    return emailObj;
                })
                .collect(Collectors.toList());
        contact.setEmails(emails);

        List<PhoneNumber> phoneNumbers = contactDTO.getPhoneNumbers().stream()
                .map(phoneNumber -> {
                    PhoneNumber phoneNumberObj = new PhoneNumber();
                    phoneNumberObj.setPhoneNumber(phoneNumber);
                    phoneNumberObj.setContact(contact);
                    return phoneNumberObj;
                })
                .collect(Collectors.toList());
        contact.setPhoneNumbers(phoneNumbers);
    }
}

