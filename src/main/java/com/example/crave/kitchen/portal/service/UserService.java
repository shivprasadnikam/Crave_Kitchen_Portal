package com.example.crave.kitchen.portal.service;

import com.example.crave.kitchen.portal.dto.UserDto;
import com.example.crave.kitchen.portal.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User createUser(UserDto userDto);

    Optional<User> updateUser(Long id, UserDto userDto);

    boolean deleteUser(Long id);

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);
}