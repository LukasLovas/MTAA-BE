package com.example.mtaa.service;

import com.example.mtaa.dto.CurrencyDTO;
import com.example.mtaa.dto.UserDTO;
import com.example.mtaa.model.CommonException;
import com.example.mtaa.model.User;
import com.example.mtaa.model.enums.CurrencyEnum;
import com.example.mtaa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public com.example.mtaa.model.User registerUser(UserDTO userInput) {
        if (userRepository.findByUsername(userInput.getUsername()).isPresent()) {
            throw new CommonException(HttpStatus.BAD_REQUEST,"User already exists with username: " + userInput.getUsername());
        }
        User user = new User();
        user.setUsername(userInput.getUsername());
        user.setPassword(passwordEncoder.encode(userInput.getPassword()));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.mtaa.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND,"User not found: " + username));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND,"User not found: " + username));
    }

    public User findUserById(Long id){
        return userRepository.findUserById(id).orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND,"User with ID " + id + "not found"));
    }

    public User findCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CommonException(HttpStatus.NOT_FOUND,"User not found: " + username));
    }

    public User setCurrency(CurrencyDTO currencyDTO) {
        User user = findCurrentUser();
        user.setCurrency(CurrencyEnum.valueOf(currencyDTO.getCurrencyCode()));

        userRepository.save(user);
        return user;
    }
}
