package com.internship.contacts.service;

import com.internship.contacts.dto.JwtRequest;
import com.internship.contacts.dto.JwtResponse;
import com.internship.contacts.dto.RegistrationUserDto;
import com.internship.contacts.dto.UserDto;
import com.internship.contacts.exception.AppError;
import com.internship.contacts.model.User;
import com.internship.contacts.utils.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;


    @Test
    void createNewUser_WithValidInput_ReturnsUserDto() {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("john");
        registrationUserDto.setPassword("password");
        registrationUserDto.setConfirmPassword("password");

        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        when(userService.findByUsername(registrationUserDto.getUsername())).thenReturn(Optional.empty());
        when(userService.createNewUser(any(RegistrationUserDto.class))).thenReturn(user);

        ResponseEntity<?> response = authService.createNewUser(registrationUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UserDto.class, response.getBody().getClass());

        UserDto userDto = (UserDto) response.getBody();
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getUsername(), userDto.getUsername());

        verify(userService).findByUsername(registrationUserDto.getUsername());
        verify(userService).createNewUser(registrationUserDto);
    }


    @Test
    void createNewUser_WithMismatchedPasswords_ReturnsBadRequest() {

        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setPassword("password");
        registrationUserDto.setConfirmPassword("differentPassword");

        ResponseEntity<?> response = authService.createNewUser(registrationUserDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(AppError.class, response.getBody().getClass());

        AppError appError = (AppError) response.getBody();
        assertEquals("Passwords don`t match", appError.getMessage());
    }

    @Test
    void createNewUser_UserAlreadyExists_ReturnsBadRequest() {
        // Arrange
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("john");
        registrationUserDto.setPassword("password");
        registrationUserDto.setConfirmPassword("password");

        when(userService.findByUsername(registrationUserDto.getUsername())).thenReturn(Optional.of(new User()));

        ResponseEntity<?> response = authService.createNewUser(registrationUserDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(AppError.class, response.getBody().getClass());

        AppError appError = (AppError) response.getBody();
        assertEquals("User already exists", appError.getMessage());

        verify(userService).findByUsername(registrationUserDto.getUsername());
    }

    @Test
    void createAuthToken_WithValidCredentials_ReturnsJwtResponse() {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("john");
        authRequest.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userService.loadUserByUsername(authRequest.getUsername())).thenReturn(userDetails);
        when(jwtTokenUtils.generateToken(userDetails)).thenReturn("jwtToken");

        ResponseEntity<?> response = authService.createAuthToken(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(JwtResponse.class, response.getBody().getClass());

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("jwtToken", jwtResponse.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenUtils).generateToken(userDetails);
    }

    @Test
    void createAuthToken_WithInvalidCredentials_ReturnsUnauthorized() {
        JwtRequest authRequest = new JwtRequest();
        authRequest.setUsername("john");
        authRequest.setPassword("invalidPassword");

        doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<?> response = authService.createAuthToken(authRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(AppError.class, response.getBody().getClass());

        AppError appError = (AppError) response.getBody();
        assertEquals("Invalid login or password", appError.getMessage());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
