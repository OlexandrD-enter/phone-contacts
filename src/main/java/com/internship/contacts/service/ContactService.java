package com.internship.contacts.service;

import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.model.Contact;
import com.internship.contacts.model.Email;
import com.internship.contacts.model.PhoneNumber;
import com.internship.contacts.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public ResponseEntity<?> getAllByUserUsername(String username){
        List<Contact> contacts = contactRepository.findByUser_Username(username);
        List<ContactDTO> contactResponses = new ArrayList<>();
        for (Contact contact : contacts) {
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setId(contact.getId());
            contactDTO.setName(contact.getName());
            contactDTO.setUserId(contact.getUser().getId());
            List<String> emailList = contact.getEmails().stream()
                    .map(Email::getEmail)
                    .collect(Collectors.toList());
            contactDTO.setEmails(emailList);
            List<String> phoneList = contact.getPhoneNumbers().stream()
                    .map(PhoneNumber::getPhoneNumber)
                    .collect(Collectors.toList());
            contactDTO.setPhoneNumbers(phoneList);
            contactResponses.add(contactDTO);
        }
        return ResponseEntity.ok(contactResponses);
    }
}
