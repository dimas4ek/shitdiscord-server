package org.shithackers.shitdiscordserver.service;

import org.shithackers.shitdiscordserver.model.user.Role;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.payload.request.RegisterRequest;
import org.shithackers.shitdiscordserver.repo.user.RoleRepo;
import org.shithackers.shitdiscordserver.repo.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RegisterService {
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder encoder;

    @Autowired
    public RegisterService(UserRepo userRepo, RoleRepo roleRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }
    
    public void registerUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        
        Set<String> strRoles = registerRequest.getRole();
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null) {
            Role userRole = roleRepo.findByName(Role.ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepo.findByName(Role.ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modRole = roleRepo.findByName(Role.ERole.ROLE_MODERATOR)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(modRole);
                    }
                    default -> {
                        Role userRole = roleRepo.findByName(Role.ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                    }
                }
            });
        }
        
        user.setRoles(roles);
        userRepo.save(user);
    }
}
