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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public void grantAdminRole(UUID userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng!"));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new Exception("Không tìm thấy quyền ROLE_ADMIN!"));

        user.getRoles().add(adminRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void revokeAdminRole(UUID userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng!"));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new Exception("Không tìm thấy quyền ROLE_ADMIN!"));

        user.getRoles().remove(adminRole);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void deleteUser(UUID userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng!"));

        // Không cho phép xóa superadmin
        boolean isSuperadmin = user.getRoles().stream()
            .anyMatch(r -> r.getName().equals("ROLE_SUPERADMIN"));
        if (isSuperadmin) {
            throw new Exception("Không thể xóa tài khoản Superadmin!");
        }

        user.setIsActive(false);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void toggleUserActive(UUID userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("Không tìm thấy người dùng!"));

        boolean isSuperadmin = user.getRoles().stream()
            .anyMatch(r -> r.getName().equals("ROLE_SUPERADMIN"));
        if (isSuperadmin) {
            throw new Exception("Không thể thay đổi trạng thái tài khoản Superadmin!");
        }

        user.setIsActive(!user.getIsActive());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
