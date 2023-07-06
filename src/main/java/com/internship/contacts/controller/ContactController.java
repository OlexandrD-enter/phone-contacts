package com.internship.contacts.controller;

import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.model.Contact;
import com.internship.contacts.model.User;
import com.internship.contacts.service.ContactService;
import com.internship.contacts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("/contacts/{id}")
    public void deleteContact(@PathVariable Long id){
        contactService.deleteById(id);
    }

    @PostMapping("/contacts")
    public ResponseEntity<?> save(@RequestBody @Valid ContactDTO contactDTO,
                                  BindingResult bindingResult,
                                  Principal principal) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ResponseEntity.badRequest().body("Validation error: " + fieldErrors.get(0).getDefaultMessage());
        }
        String currentUsername = principal.getName();
        Optional<User> optionalUser = userService.findByUsername(currentUsername);
        if (optionalUser.isPresent()) {
            return contactService.save(contactDTO, optionalUser.get());
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }
    }

    @PutMapping("/contacts/{id}")
    public ResponseEntity<?> edit(@RequestBody @Valid ContactDTO contactDTO,
                                  BindingResult bindingResult,
                                  @PathVariable Long id,
                                  Principal principal) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ResponseEntity.badRequest().body("Validation error: " + fieldErrors.get(0).getDefaultMessage());
        }
        String currentUsername = principal.getName();
        Optional<User> optionalUser = userService.findByUsername(currentUsername);
        Optional<Contact> optionalContact = contactService.findById(id);
        if (optionalContact.isPresent() && optionalUser.isPresent()) {
            return contactService.update(contactDTO, optionalContact.get(), optionalUser.get(), id);
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }
    }
}
