package com.internship.contacts.controller;

import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.model.Contact;
import com.internship.contacts.model.User;
import com.internship.contacts.service.ContactService;
import com.internship.contacts.service.UserService;
import com.internship.contacts.utils.ContactImporter;
import com.internship.contacts.utils.mapper.ContactMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
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
    public ResponseEntity<?> save(@ModelAttribute @Valid ContactDTO contactDTO,
                                  BindingResult bindingResult,
                                  Principal principal) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
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
    public ResponseEntity<?> edit(@ModelAttribute @Valid ContactDTO contactDTO,
                                  BindingResult bindingResult,
                                  @PathVariable Long id,
                                  Principal principal) {
        ResponseEntity<?> errorResponse = handleBindingErrors(bindingResult);
        if (errorResponse != null) {
            return errorResponse;
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

    @GetMapping("/contacts/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        Optional<Contact> optionalContact = contactService.findById(id);
        if (optionalContact.isPresent() && optionalContact.get().getImage() != null) {
            byte[] imageData = optionalContact.get().getImage();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<?> handleBindingErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return ResponseEntity.badRequest().body("Validation error: " + fieldErrors.get(0).getDefaultMessage());
        }
        return null;
    }

    @GetMapping("/contacts/export")
    public ResponseEntity<?> exportContacts(HttpServletResponse response) {
        try {
            String filePath = "contacts.json";
            contactService.exportContactsToJsonFile(filePath);
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=contacts.json");
            Files.copy(Paths.get(filePath), response.getOutputStream());
            response.flushBuffer();
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to export contacts: " + e.getMessage());
        }
    }

    @PostMapping("/contacts/import")
    public ResponseEntity<?> importContacts(@RequestParam("file") MultipartFile file, Principal principal) {
        List<Contact> importedContacts = new ArrayList<>();
        try {
            Optional<User> optionalUser = userService.findByUsername(principal.getName());
            if (optionalUser.isPresent()){
                List<ContactDTO> contactDTOs = ContactImporter.importFromJson(file);
                importedContacts = contactService.importContacts(contactDTOs, optionalUser.get());
            }
            return ResponseEntity.ok("Successfully imported " + importedContacts.size() + " contacts.");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to import contacts: " + e.getMessage());
        }
    }
}
