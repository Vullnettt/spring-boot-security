package com.spring.oauth2.springsecurity.config;

import com.spring.oauth2.springsecurity.role.Role;
import com.spring.oauth2.springsecurity.role.RoleRepository;
import com.spring.oauth2.springsecurity.user.Provider;
import com.spring.oauth2.springsecurity.user.User;
import com.spring.oauth2.springsecurity.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class CommandLineRunnerConfig {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CommandLineRunnerConfig(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner commandLineRunner(){

        if(roleRepository.findAll().isEmpty() || userRepository.findAll().isEmpty()) {
            return  args -> {
                Role role = Role.builder()
                        .name("USER")
                        .build();
                roleRepository.save(role);

                Role role1 = Role.builder()
                        .name("ADMIN")
                        .build();
                roleRepository.save(role1);

                Set<Role> roles = new HashSet<>();
                roles.add(role1);

                User user = User.builder()
                        .username("admin@gmail.com")
                        .password(passwordEncoder.encode("admin123"))
                        .isEnabled(true)
                        .roles(roles)
                        .accountNonExpired(true)
                        .accountNonLocked(true)
                        .credentialsNonExpired(true)
                        .providerId(Provider.local.name())
                        .build();
                userRepository.save(user);
            };
        }

        return null;
    }
}
