package com.schoolmanager.graduation_backend.config;

import com.schoolmanager.graduation_backend.security.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditConfig {

    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return new AuditorAware<>() {
            @Override
            public @NonNull Optional<UUID> getCurrentAuditor() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                    return Optional.empty();
                }

                if (!(auth.getPrincipal() instanceof CustomUserDetails userDetails) || userDetails.getId() == null) {
                    return Optional.empty();
                }

                return Optional.of(userDetails.getId());
            }
        };
    }
}
