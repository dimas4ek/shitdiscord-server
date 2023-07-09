package org.shithackers.shitdiscordserver.controller;

import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.service.PeopleService;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class PeopleController {
    private final PeopleService userService;

    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.userService = peopleService;
    }

    @GetMapping()
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/user")
    public User currentUser() {
        return AuthUtils.getPerson();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getOneUser(id);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> editUser(@PathVariable int id, @RequestBody User user) {
        return new ResponseEntity<>(userService.editRest(id, user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        String username = userService.getOneUser(id).getUsername();
        userService.delete(id);
        return ResponseEntity.ok("User " + username + " has been successfully deleted");
    }
}
