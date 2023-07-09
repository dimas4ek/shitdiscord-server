package org.shithackers.shitdiscordserver.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.shithackers.shitdiscordserver.payload.request.auth.LoginRequest;
import org.shithackers.shitdiscordserver.payload.request.auth.RegisterRequest;
import org.shithackers.shitdiscordserver.payload.response.MessageResponse;
import org.shithackers.shitdiscordserver.payload.response.UserInfoResponse;
import org.shithackers.shitdiscordserver.repo.user.UserRepo;
import org.shithackers.shitdiscordserver.security.jwt.JwtUtils;
import org.shithackers.shitdiscordserver.security.services.UserDetailsImpl;
import org.shithackers.shitdiscordserver.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final RegisterService registerService;
    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;
    
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, RegisterService registerService, UserRepo userRepo, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.registerService = registerService;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                    loginRequest.getPassword()
            );
        
        try {
            Authentication auth = authenticationManager.authenticate(authToken);
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            
            List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
            
            String token = jwtUtils.generateToken(loginRequest.getUsername());
            
            return ResponseEntity.ok()
                .body(new UserInfoResponse(userDetails.getId(),
                                           userDetails.getUsername(),
                                           userDetails.getEmail(),
                                           roles,
                                           token)
                );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Bad credentials"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUSer(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepo.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Username is already taken!"));
        }
        
        if (userRepo.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Email is already in use!"));
        }
        
        registerService.registerUser(registerRequest);
        
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        boolean isSecure = false;
        String contextPath = null;
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            isSecure = request.isSecure();
            contextPath = request.getContextPath();
        }
        SecurityContext context = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();
        context.setAuthentication(null);
        if (response != null) {
            Cookie cookie = new Cookie("JSESSIONID", null);
            String cookiePath = StringUtils.hasText(contextPath) ? contextPath : "/";
            cookie.setPath(cookiePath);
            cookie.setMaxAge(0);
            cookie.setSecure(isSecure);
            response.addCookie(cookie);
        }
        
        return ResponseEntity.ok()
            .body(new MessageResponse("You've been logged out!"));
    }
}
