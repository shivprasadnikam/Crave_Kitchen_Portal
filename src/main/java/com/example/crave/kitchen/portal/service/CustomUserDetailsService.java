package com.example.crave.kitchen.portal.service;

import com.example.crave.kitchen.portal.entity.VendorsEntity;
import com.example.crave.kitchen.portal.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private VendorRepository vendorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        VendorsEntity vendorsEntity = vendorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(vendorsEntity.getEmail())
                .password(vendorsEntity.getPasswordHash())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_VENDOR")))
                .accountExpired(false)
                .accountLocked(!vendorsEntity.getIsActive())
                .credentialsExpired(false)
                .disabled(!vendorsEntity.getIsEmailVerified())
                .build();
    }
}