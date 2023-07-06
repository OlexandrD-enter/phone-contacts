package com.internship.contacts.service;

import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.exception.ContactValidationException;
import com.internship.contacts.model.Contact;
import com.internship.contacts.model.Email;
import com.internship.contacts.model.PhoneNumber;
import com.internship.contacts.model.User;
import com.internship.contacts.repository.ContactRepository;
import com.internship.contacts.repository.EmailRepository;
import com.internship.contacts.repository.PhoneNumberRepository;
import com.internship.contacts.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContactServiceTest {

    @Autowired
    private ContactService contactService;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    void getAllByUserUsername_ExistingUser_ReturnsContactDTOList() {
        User user = new User();
        user.setUsername("user_alex");
        User savedUser = userRepository.save(user);

        Contact contact1 = new Contact();
        contact1.setName("Alex");
        contact1.setUser(savedUser);

        Email email1 = new Email();
        email1.setEmail("alex.dem@gmail.com");
        email1.setContact(contact1);
        emailRepository.save(email1);

        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setPhoneNumber("+380677077777");
        phoneNumber1.setContact(contact1);
        phoneNumberRepository.save(phoneNumber1);

        contact1.setEmails(Collections.singletonList(email1));
        contact1.setPhoneNumbers(Collections.singletonList(phoneNumber1));
        contactRepository.save(contact1);

        ResponseEntity<?> response = contactService.getAllByUserUsername("user_alex");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<ContactDTO> contactDTOList = (List<ContactDTO>) response.getBody();
        assertEquals(1, contactDTOList.size());

        assertEquals("Alex", contactDTOList.get(0).getName());
        assertEquals(1, contactDTOList.get(0).getEmails().size());
        assertEquals("alex.dem@gmail.com", contactDTOList.get(0).getEmails().get(0));
        assertEquals(1, contactDTOList.get(0).getPhoneNumbers().size());
        assertEquals("+380677077777", contactDTOList.get(0).getPhoneNumbers().get(0));
    }

    @Test
    void save_ExistingContactName_ThrowsContactValidationException() {
        User user = new User();
        user.setUsername("alex_user");
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setName("Alex");
        contact.setUser(user);
        contactRepository.save(contact);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName("Alex");

        assertThrows(ContactValidationException.class, () -> contactService.save(contactDTO, user));
    }

    @Test
    void save_ExistingContactEmail_ThrowsContactValidationException() {
        User user = new User();
        user.setUsername("alex_user");
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setName("Alex");
        contact.setUser(user);
        contactRepository.save(contact);

        Email email = new Email();
        email.setEmail("alex.dem@gmail.com");
        email.setContact(contact);
        emailRepository.save(email);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName("Alex2");
        contactDTO.setEmails(Collections.singletonList("alex.dem@gmail.com"));

        assertThrows(ContactValidationException.class, () -> contactService.save(contactDTO, user));
    }

    @Test
    void save_ExistingContactPhoneNumber_ThrowsContactValidationException() {
        User user = new User();
        user.setUsername("alex_user");
        userRepository.save(user);

        Contact contact = new Contact();
        contact.setName("Alex");
        contact.setUser(user);
        contactRepository.save(contact);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPhoneNumber("+380677077777");
        phoneNumber.setContact(contact);
        phoneNumberRepository.save(phoneNumber);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName("Alex2");
        contactDTO.setPhoneNumbers(Collections.singletonList("+380677077777"));

        assertThrows(ContactValidationException.class, () -> contactService.save(contactDTO, user));
    }


    @Test
    void getAllByUserUsername_NonExistingUser_ReturnsEmptyList() {
        ResponseEntity<?> response = contactService.getAllByUserUsername("nonExisting");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);

        List<ContactDTO> contactDTOList = (List<ContactDTO>) response.getBody();
        assertTrue(contactDTOList.isEmpty());
    }

    @Test
    void save_ValidContactDTOAndUser_ReturnsCreatedResponse() {
        User user = new User();
        user.setUsername("alex_user");
        User savedUser = userRepository.save(user);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName("Alex");
        contactDTO.setEmails(Collections.singletonList("alex.dem@gmail.com"));
        contactDTO.setPhoneNumbers(Collections.singletonList("+380677077777"));

        ResponseEntity<?> response = contactService.save(contactDTO, savedUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Contact saved successfully.", response.getBody());

        List<Contact> savedContacts = contactRepository.findByUser_Username("alex_user");
        assertEquals(1, savedContacts.size());

        Contact savedContact = savedContacts.get(0);
        assertEquals("Alex", savedContact.getName());

        List<Email> savedEmails = emailRepository.findAllByContactUser(user);
        assertEquals(1, savedEmails.size());
        assertEquals("alex.dem@gmail.com", savedEmails.get(0).getEmail());

        List<PhoneNumber> savedPhoneNumbers = phoneNumberRepository.findAllByContactUser(user);
        assertEquals(1, savedPhoneNumbers.size());
        assertEquals("+380677077777", savedPhoneNumbers.get(0).getPhoneNumber());
    }

    @Test
    void update_ExistingContactName_ThrowsContactValidationException() {
        User user = new User();
        user.setUsername("john");
        userRepository.save(user);

        Contact existingContact = new Contact();
        existingContact.setName("John Doe");
        existingContact.setUser(user);
        contactRepository.save(existingContact);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName("John Doe");

        assertThrows(ContactValidationException.class, () -> contactService.update(contactDTO, existingContact, user, 2L));
    }

    @Transactional
    @Test
    void update_ExistingContactWithEmail_ThrowsContactValidationException() {
        User user = new User();
        user.setUsername("user_alex");
        User savedUser = userRepository.save(user);

        Contact contact1 = new Contact();
        contact1.setName("Alex");
        contact1.setUser(savedUser);

        Email email1 = new Email();
        email1.setEmail("alex.dem@gmail.com");
        email1.setContact(contact1);
        emailRepository.save(email1);

        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setPhoneNumber("+380677077777");
        phoneNumber1.setContact(contact1);
        phoneNumberRepository.save(phoneNumber1);

        contact1.setEmails(Collections.singletonList(email1));
        contact1.setPhoneNumbers(Collections.singletonList(phoneNumber1));
        contactRepository.save(contact1);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName("Test");
        contactDTO.setEmails(Collections.singletonList("alex.dem@gmail.com"));

        Contact exist = contactRepository.findByName("Alex").get();

        ContactValidationException exception = assertThrows(ContactValidationException.class, () ->
                contactService.update(contactDTO, exist, user, 1L));
        assertEquals("You already have a contact with such email", exception.getMessage());
    }

    @Transactional
    @Test
    void update_ExistingContactWithPhoneNumber_ThrowsContactValidationException() {
        User user = new User();
        user.setUsername("user_alex");
        User savedUser = userRepository.save(user);

        Contact contact1 = new Contact();
        contact1.setName("Alex");
        contact1.setUser(savedUser);

        Email email1 = new Email();
        email1.setEmail("alex.dem@gmail.com");
        email1.setContact(contact1);
        emailRepository.save(email1);

        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setPhoneNumber("+380677077777");
        phoneNumber1.setContact(contact1);
        phoneNumberRepository.save(phoneNumber1);

        contact1.setEmails(Collections.singletonList(email1));
        contact1.setPhoneNumbers(Collections.singletonList(phoneNumber1));
        contactRepository.save(contact1);

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setName("Test");
        contactDTO.setPhoneNumbers(Collections.singletonList("+380677077777"));

        Contact exist = contactRepository.findByName("Alex").get();

        ContactValidationException exception = assertThrows(ContactValidationException.class, () ->
                contactService.update(contactDTO, exist, user, 1L));
        assertEquals("You already have a contact with such phone number", exception.getMessage());
    }
}