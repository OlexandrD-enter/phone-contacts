package com.internship.contacts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.exception.ContactValidationException;
import com.internship.contacts.model.Contact;
import com.internship.contacts.model.Email;
import com.internship.contacts.model.PhoneNumber;
import com.internship.contacts.model.User;
import com.internship.contacts.repository.ContactRepository;
import com.internship.contacts.repository.EmailRepository;
import com.internship.contacts.repository.PhoneNumberRepository;
import com.internship.contacts.utils.Constants;
import com.internship.contacts.utils.CustomMultipartFile;
import com.internship.contacts.utils.mapper.ContactMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContactService {
    private final ContactRepository contactRepository;
    private final EmailRepository emailRepository;
    private final PhoneNumberRepository phoneNumberRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository, EmailRepository emailRepository, PhoneNumberRepository phoneNumberRepository) {
        this.contactRepository = contactRepository;
        this.emailRepository = emailRepository;
        this.phoneNumberRepository = phoneNumberRepository;
    }

    public ResponseEntity<?> getAllByUserUsername(String username) {
        List<Contact> contacts = contactRepository.findByUser_Username(username);
        List<ContactDTO> contactResponses = new ArrayList<>();
        for (Contact contact : contacts) {
            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setName(contact.getName());
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

    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }

    public ResponseEntity<?> save(ContactDTO contactDTO, User user) {
        checkIfContactDataAlreadyExists(contactDTO, user);

        Contact contact = new Contact();
        contact.setName(contactDTO.getName());
        contact.setUser(user);
        setImageToContact(contactDTO, contact);

        ContactMapper.ListsEmailsAndPhonesToStringNames(contactDTO, contact);
        contactRepository.save(contact);

        return ResponseEntity.status(HttpStatus.CREATED).body("Contact saved successfully.");
    }

    private void checkIfContactDataAlreadyExists(ContactDTO contactDTO, User user) {
        Optional<Contact> optionalContact = contactRepository.findByNameAndUserUsername(contactDTO.getName(), user.getUsername());
        if (optionalContact.isPresent()) {
            throw new ContactValidationException("Contact with name '" + contactDTO.getName() + "' already exists in your phone");
        }
        if (isUserAlreadyHaveContactWithEmails(contactDTO, user)) {
            throw new ContactValidationException("You already have a contact with such email");
        }
        if (isUserAlreadyHaveContactWithPhones(contactDTO, user)) {
            throw new ContactValidationException("You already have a contact with such phone number");
        }
    }

    private void setImageToContact(ContactDTO contactDTO, Contact contact) {
        try {
            if (contactDTO.getImageFile() != null && !contactDTO.getImageFile().isEmpty()) {
                if(contactDTO.getImageFile().getSize() > Constants.MAX_IMAGE_SIZE){
                    throw new ContactValidationException("Too large file, choose another");
                }
                byte[] imageData = contactDTO.getImageFile().getBytes();
                contact.setImage(imageData);
            }
        }catch (IOException e){
            throw new ContactValidationException(e.getMessage());
        }
    }

    private boolean isUserAlreadyHaveContactWithEmails(ContactDTO contactDTO, User user) {
        List<Email> existingEmails = emailRepository.findAllByContactUser(user);
        List<String> emailList = existingEmails.stream()
                .map(Email::getEmail)
                .collect(Collectors.toList());
        return CollectionUtils.containsAny(emailList, contactDTO.getEmails());
    }

    private boolean isUserAlreadyHaveContactWithPhones(ContactDTO contactDTO, User user) {
        List<PhoneNumber> existingPhones = phoneNumberRepository.findAllByContactUser(user);
        List<String> phonesList = existingPhones.stream()
                .map(PhoneNumber::getPhoneNumber)
                .collect(Collectors.toList());
        return CollectionUtils.containsAny(phonesList, contactDTO.getPhoneNumbers());
    }

    public Optional<Contact> findByName(String name) {
        return contactRepository.findByName(name);
    }

    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }

    @Transactional
    public ResponseEntity<?> update(ContactDTO contactDTO, Contact contact, User user, Long id) {
        Optional<Contact> optionalContact = contactRepository.findByNameAndUserUsername(contactDTO.getName(), user.getUsername());
        if (optionalContact.isPresent() && !optionalContact.get().getId().equals(id)) {
            throw new ContactValidationException("Contact with name '" + contactDTO.getName() + "' already exists in your phone");
        }

        emailRepository.deleteAllByContact(contact);
        phoneNumberRepository.deleteAllByContact(contact);

        if (isUserAlreadyHaveContactWithEmails(contactDTO, user)) {
            throw new ContactValidationException("You already have a contact with such email");
        }

        if (isUserAlreadyHaveContactWithPhones(contactDTO, user)) {
            throw new ContactValidationException("You already have a contact with such phone number");
        }

        contact.setName(contactDTO.getName());

        List<Email> emails = contactDTO.getEmails().stream()
                .map(email -> {
                    Email emailObj = new Email();
                    emailObj.setEmail(email);
                    emailObj.setContact(contact);
                    return emailObj;
                })
                .collect(Collectors.toList());
        contact.getEmails().addAll(emails);

        List<PhoneNumber> phoneNumbers = contactDTO.getPhoneNumbers().stream()
                .map(phoneNumber -> {
                    PhoneNumber phoneNumberObj = new PhoneNumber();
                    phoneNumberObj.setPhoneNumber(phoneNumber);
                    phoneNumberObj.setContact(contact);
                    return phoneNumberObj;
                })
                .collect(Collectors.toList());
        contact.getPhoneNumbers().addAll(phoneNumbers);
        setImageToContact(contactDTO, contact);
        contactRepository.save(contact);
        return ResponseEntity.ok("Contact updated successfully.");
    }

    public void exportContactsToJsonFile(String filePath) throws IOException {
        List<Contact> contacts = contactRepository.findAll();
        List<ContactDTO> contactDTOs = contacts.stream()
                .map(ContactMapper::toDTO)
                .collect(Collectors.toList());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(filePath), contactDTOs);
    }

    public List<Contact> importContacts(List<ContactDTO> contactDTOs, User user) {
        List<Contact> importedContacts = new ArrayList<>();
        for (ContactDTO contactDTO : contactDTOs) {
            validateContactDTO(contactDTO);
            Contact contact = ContactMapper.toEntity(contactDTO, user);
            checkIfContactDataAlreadyExists(contactDTO, user);
            importedContacts.add(contact);
        }
        return contactRepository.saveAll(importedContacts);
    }

    private void validateContactDTO(ContactDTO contactDTO) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<ContactDTO>> violations = validator.validate(contactDTO);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<ContactDTO> violation : violations) {
                log.debug(violation.getMessage());
            }
            throw new ContactValidationException("Invalid contact data when importing from json");
        }
    }
}
