package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));
    }

    public void register(String username, String rawPassword, String fullName, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("このユーザー名は既に使用されています");
        }
        if (!isValidRole(role)) {
            throw new IllegalArgumentException("不正なユーザタイプです");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setRole(role);
        user.setEnabled(true);
        userRepository.save(user);
    }

    private boolean isValidRole(String role) {
        return role.equals("ROLE_ADMIN")
                || role.equals("ROLE_TEACHER")
                || role.equals("ROLE_STUDENT");
    }
}
