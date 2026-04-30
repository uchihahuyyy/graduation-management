package com.schoolmanager.graduation_backend.seeder;

import com.schoolmanager.graduation_backend.entity.Role;
import com.schoolmanager.graduation_backend.entity.User;
import com.schoolmanager.graduation_backend.repository.RoleRepository;
import com.schoolmanager.graduation_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed Roles
        Role superadminRole = createRoleIfNotFound("ROLE_SUPERADMIN");
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
        Role userRole = createRoleIfNotFound("ROLE_USER");

        // Seed Superadmin
        if (!userRepository.existsByUsername("superadmin")) {
            User superadmin = new User();
            superadmin.setUsername("superadmin");
            superadmin.setPassword(passwordEncoder.encode("superadmin"));
            superadmin.setFullName("System Super Administrator");
            superadmin.setCreatedAt(LocalDateTime.now());
            superadmin.setIsActive(true);
            
            Set<Role> roles = new HashSet<>();
            roles.add(superadminRole);
            superadmin.setRoles(roles);
            
            userRepository.save(superadmin);
            System.out.println("Superadmin account seeded successfully!");
        }
    }

    private Role createRoleIfNotFound(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setCreatedAt(LocalDateTime.now());
            role.setIsActive(true);
            return roleRepository.save(role);
        });
    }
}
