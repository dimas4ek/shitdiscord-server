package org.shithackers.shitdiscordserver.utils;

import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.repo.user.UserRepo;
import org.shithackers.shitdiscordserver.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    private static UserRepo peopleRepo = null;
    
    @Autowired
    public AuthUtils(UserRepo peopleRepo) {
        AuthUtils.peopleRepo = peopleRepo;
    }
    
    public static boolean checkLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }
    
    public static int getPersonId() {
        if (checkLoggedIn()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl personDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            return personDetails.getId();
        }
        return 0;
    }
    
    public static User getPerson() {
        if (checkLoggedIn()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl personDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            return peopleRepo.findById(personDetails.getId()).orElse(null);
        }
        return null;
    }
}
