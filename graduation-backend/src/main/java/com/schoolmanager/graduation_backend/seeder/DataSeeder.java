package com.schoolmanager.graduation_backend.seeder;

import com.schoolmanager.graduation_backend.entity.Role;
import com.schoolmanager.graduation_backend.entity.Program;
import com.schoolmanager.graduation_backend.entity.User;
import com.schoolmanager.graduation_backend.repository.ProgramRepository;
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

    @Autowired
    private ProgramRepository programRepository;

    @Override
    public void run(String... args) throws Exception {
        // Seed Roles
        Role superadminRole = createRoleIfNotFound("ROLE_SUPERADMIN");
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");

        seedDefaultProgram();

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

    private void seedDefaultProgram() {
        if (programRepository.existsByProgramCode("CNTT")) {
            return;
        }

        Program program = new Program();
        program.setProgramCode("CNTT");
        program.setProgramName("Chương trình Công nghệ thông tin");
        program.setMajorName("Công nghệ thông tin");
        program.setEducationLevel("Đại học");
        program.setTotalRequiredCredits(120);
        program.setCreatedAt(LocalDateTime.now());
        program.setIsActive(true);
        programRepository.save(program);
    }
}
