package com.example.crave.kitchen.portal.impl;

import com.example.crave.kitchen.portal.dto.UserDto;
import com.example.crave.kitchen.portal.entity.User;
import com.example.crave.kitchen.portal.repository.UserRepository;
import com.example.crave.kitchen.portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(userDto.getPassword() != null ? userDto.getPassword() : "defaultPassword"); // In
                                                                                                         // production,
                                                                                                         // this should
                                                                                                         // be encrypted
        user.setRole(userDto.getRole() != null ? User.UserRole.valueOf(userDto.getRole()) : User.UserRole.vendor);
        user.setPhone(userDto.getPhone());
        user.setProfileImageUrl(userDto.getProfileImageUrl());
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(user -> {
                    if (userDto.getName() != null)
                        user.setName(userDto.getName());
                    if (userDto.getEmail() != null)
                        user.setEmail(userDto.getEmail());
                    if (userDto.getPassword() != null)
                        user.setPasswordHash(userDto.getPassword());
                    if (userDto.getRole() != null)
                        user.setRole(User.UserRole.valueOf(userDto.getRole()));
                    if (userDto.getPhone() != null)
                        user.setPhone(userDto.getPhone());
                    if (userDto.getProfileImageUrl() != null)
                        user.setProfileImageUrl(userDto.getProfileImageUrl());
                    return userRepository.save(user);
                });
    }

    @Override
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}