package com.bprflavorshub.bpr_flavors_hub.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String phone)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByPhone(phone)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with phone: " + phone
                        )
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhone())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}