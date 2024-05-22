package com.user.connect.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.user.connect.entity.user.User;
import com.user.connect.exception.external.UserNotFoundException;
import com.user.connect.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class DetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User with " + username + " not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(foundUser.getEmail())
                .password(foundUser.getPassword())
                .roles(foundUser.getRole().toString())
                .build();
    }
}
