package com.example.demo.service;

import com.example.demo.model.persistence.User;

import java.security.NoSuchAlgorithmException;

public interface UserService {
    User findUser(String username);
    User generatePasswordAndPersistUser(User user) throws NoSuchAlgorithmException;
    User persistUser(User user);
    void validatePassword(String password, String confirmPassword, String username);
}
