package com.internship.contacts.service;

import com.internship.contacts.dto.RegistrationUserDto;
import com.internship.contacts.model.User;
import com.internship.contacts.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_ExistingUser_ReturnsUser() {
        User user = new User();
        user.setUsername("Alex");
        user.setPassword("password");
        userRepository.save(user);

        User result = userService.findByUsername("Alex").orElse(null);

        assertNotNull(result);
        assertEquals("Alex", result.getUsername());
        assertEquals("password", result.getPassword());
    }

    @Test
    void findByUsername_NonExistingUser_ReturnsEmptyOptional() {
        User result = userService.findByUsername("nonExisting").orElse(null);

        assertNull(result);
    }

    @Test
    void loadUserByUsername_ExistingUser_ReturnsUserDetails() {
        String rawPassword = "password";
        User user = new User();
        user.setUsername("Alex");
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        UserDetails userDetails = userService.loadUserByUsername("Alex");

        assertNotNull(userDetails);
        assertEquals("Alex", userDetails.getUsername());

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        assertTrue(passwordEncoder.matches(rawPassword, userDetails.getPassword()));
    }

    @Test
    void loadUserByUsername_NonExistingUser_ThrowsUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonExisting"));
    }

    @Test
    void createNewUser_ValidRegistrationUserDto_CreatesUser() {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("Alex");
        registrationUserDto.setPassword("password");

        User result = userService.createNewUser(registrationUserDto);

        assertNotNull(result);
        assertEquals("Alex", result.getUsername());
        assertTrue(new BCryptPasswordEncoder().matches("password", result.getPassword()));
    }
}

