package com.example.demo.service;

import com.example.demo.controllers.exception.InvalidPasswordException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class UserServiceImpl implements UserService {

    private Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public User persistUser(User user) {
        log.info("User name set with {}", user.getUsername());
        return userRepository.save(user);
    }

    @Override
    public User findUser(String username) {
        User user =  userRepository.findByUsername(username);

        if (user == null) {
            log.info("Username {} was not found in the database", username);
        }

        return user;
    }

    @Override
    public User generatePasswordAndPersistUser(User user) throws NoSuchAlgorithmException {
        user.setPassword(encoder.encode(user.getPassword()));
        return persistUser(user);
    }

    @Override
    public void validatePassword(String password, String confirmPassword, String username) throws InvalidPasswordException {
        if (password.length() < 7 ||
                !password.equals(confirmPassword)) {
            log.error("Error with user password. Cannot create user {}", username);
            throw new InvalidPasswordException("Password cannot be created.");
        }
    }

}
