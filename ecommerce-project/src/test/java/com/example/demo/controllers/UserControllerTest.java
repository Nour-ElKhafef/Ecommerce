package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private UserService userService;

    @Test
    public void createUser_happyPath() throws Exception {
        when(encoder.encode(anyString())).thenReturn("hashed");
        when(cartRepository.save(any())).thenReturn(null);
        when(userRepository.save(any())).thenReturn(null);

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Nour");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("Nour", u.getUsername());
        assertEquals("hashed", u.getPassword());
    }

    @Test
    public void createUser_invalidPassword() throws Exception {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Nour");
        r.setPassword("test");
        r.setConfirmPassword("test");

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findUserById_happy() {
        User user = new User();
        user.setId(0);
        user.setUsername("Nour");
        user.setPassword("password");
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findById(0L)).thenReturn(optionalUser);

        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final User body = response.getBody();
        assertNotNull(body);
        assertEquals(0L, body.getId());
        assertEquals(user.getPassword(), body.getPassword());
        assertEquals(user.getUsername(), body.getUsername());
    }

    @Test
    public void findUserByUserName_happy() {
        User user = new User();
        user.setId(0);
        user.setUsername("Nour");
        user.setPassword("password");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final User body = response.getBody();
        assertNotNull(body);
        assertEquals(0L, body.getId());
        assertEquals(user.getPassword(), body.getPassword());
        assertEquals(user.getUsername(), body.getUsername());
    }
}