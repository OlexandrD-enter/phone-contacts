package com.internship.contacts.controller;

import com.internship.contacts.service.ContactService;
import com.internship.contacts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class ContactController {
    private final ContactService contactService;
    private final UserService userService;

    @Autowired
    public ContactController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    @GetMapping("/contacts")
    public ResponseEntity<?> getAllContacts(Principal principal) {
       return contactService.getAllByUserUsername(principal.getName());
    }
}
