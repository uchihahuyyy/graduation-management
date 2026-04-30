package com.schoolmanager.graduation_backend.service;

import com.schoolmanager.graduation_backend.entity.Role;
import com.schoolmanager.graduation_backend.entity.User;
import com.schoolmanager.graduation_backend.repository.RoleRepository;
import com.schoolmanager.graduation_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void registerUser(String username, String password, String email, String fullName) throws Exception {
        if (userRepository.existsByUsername(username)) {
            throw new Exception("Tên đăng nhập đã tồn tại!");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new Exception("Không tìm thấy quyền mặc định (ROLE_USER). Hãy kiểm tra DataSeeder."));
            
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        
        userRepository.save(user);
    }
}
