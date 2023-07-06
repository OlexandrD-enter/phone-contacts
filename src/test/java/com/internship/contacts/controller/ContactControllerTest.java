package com.internship.contacts.controller;

import com.internship.contacts.dto.ContactDTO;
import com.internship.contacts.model.User;
import com.internship.contacts.service.ContactService;
import com.internship.contacts.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {
    @InjectMocks
    private ContactController contactController;

    @Mock
    private ContactService contactService;

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Principal principal;

    @Test
    void deleteContact_ShouldDeleteContact() {
        Long contactId = 1L;

        contactController.deleteContact(contactId);

        verify(contactService, times(1)).deleteById(contactId);
    }

    @Test
    void save_WithValidContactDTO_ShouldSaveContact() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        ContactDTO contactDTO = new ContactDTO();
        BindingResult bindingResult = mock(BindingResult.class);
        Principal principal = mock(Principal.class);
        ResponseEntity r = ResponseEntity.status(HttpStatus.OK).body("Contact saved successfully.");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(principal.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(contactService.save(contactDTO, user)).thenReturn(r);

        ResponseEntity<?> response = contactController.save(contactDTO, bindingResult, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(r, response);
        assertEquals(response.getBody(), r.getBody());
        verify(userService, times(1)).findByUsername(username);
        verify(contactService, times(1)).save(contactDTO, user);
    }

    @Test
    void save_WithInvalidContactDTO_ShouldReturnBadRequest() {
        String errorMessage = "Invalid contact";
        ContactDTO contactDTO = new ContactDTO();
        FieldError fieldError = new FieldError("contactDTO", "fieldName", errorMessage);
        List<FieldError> fieldErrors = Arrays.asList(fieldError);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<?> response = contactController.save(contactDTO, bindingResult, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error: " + errorMessage, response.getBody());
    }

    @Test
    void save_WithNonexistentUser_ShouldReturnUnauthorized() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        ContactDTO contactDTO = new ContactDTO();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(principal.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = contactController.save(contactDTO, bindingResult, principal);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
        verify(userService, times(1)).findByUsername(username);
    }

    @Test
    void edit_WithInvalidContactDTO_ShouldReturnBadRequest() {
        String errorMessage = "Invalid contact";
        Long contactId = 1L;
        ContactDTO contactDTO = new ContactDTO();
        FieldError fieldError = new FieldError("contactDTO", "fieldName", errorMessage);
        List<FieldError> fieldErrors = Arrays.asList(fieldError);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        ResponseEntity<?> response = contactController.edit(contactDTO, bindingResult, contactId, principal);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error: " + errorMessage, response.getBody());
    }

    @Test
    void edit_WithNonexistentUserOrContact_ShouldReturnUnauthorized() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        Long contactId = 1L;
        ContactDTO contactDTO = new ContactDTO();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(principal.getName()).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(contactService.findById(contactId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = contactController.edit(contactDTO, bindingResult, contactId, principal);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
        verify(userService, times(1)).findByUsername(username);
        verify(contactService, times(1)).findById(contactId);
    }
}

