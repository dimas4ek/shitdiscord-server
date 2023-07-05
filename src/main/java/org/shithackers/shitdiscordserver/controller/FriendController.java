package org.shithackers.shitdiscordserver.controller;

import org.shithackers.shitdiscordserver.service.FriendService;
import org.shithackers.shitdiscordserver.service.PeopleService;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class FriendController {
    private final FriendService friendService;
    private final PeopleService peopleService;
    
    @Autowired
    public FriendController(FriendService friendService, PeopleService peopleService) {
        this.friendService = friendService;
        this.peopleService = peopleService;
    }
    
    @GetMapping("/friends")
    public Map<String, Object> mainPage() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        if (AuthUtils.checkLoggedIn()) {
            map.put("incomingFriendRequests", friendService.getIncomingPendingFriendRequestsList());
            map.put("outgoingFriendRequests", friendService.getOutgoingPendingFriendRequestsList());
            
            result.put("friendRequests", map);
            
            try {
                result.put("friendList", friendService.getFriendList());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        
        return result;
    }
    
    @PostMapping("/friendRequest")
    public ResponseEntity<String> friendRequest(@RequestParam("friendName") String friendName) {
        if (friendService.alreadyFriendRequest(friendName)) {
            return ResponseEntity.ok("Friend request to " + friendName + " already sent");
        }
        if (friendService.isFriend(friendName)) {
            return ResponseEntity.ok("You are already friends with " + friendName);
        }
        
        friendService.friendRequest(friendName);
        
        return ResponseEntity.ok("Friend request sent to " + friendName);
    }
    
    @PostMapping("/acceptFriendRequest")
    public ResponseEntity<String> acceptFriendRequest(@RequestParam("requestId") int requestId) {
        if (friendService.requestFromLoggedUser(requestId).equals("receiver")) {
            friendService.acceptFriendRequest(requestId);
            return ResponseEntity.ok("Incoming friend request accepted.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request ID: " + requestId);
        }
    }
    
    @PostMapping("/declineFriendRequest")
    public ResponseEntity<String> declineFriendRequest(@RequestParam("requestId") int requestId) {
        if (friendService.requestFromLoggedUser(requestId).equals("receiver")) {
            friendService.declineFriendRequest(requestId);
            return ResponseEntity.ok("Incoming friend request declined.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request ID: " + requestId);
        }
    }
    
    @PostMapping("/cancelFriendRequest")
    public ResponseEntity<String> cancelFriendRequest(@RequestParam("requestId") int requestId) {
        if (friendService.requestFromLoggedUser(requestId).equals("sender")) {
            friendService.cancelFriendRequest(requestId);
            return ResponseEntity.ok("Outgoing friend request cancelled.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request ID: " + requestId);
        }
    }
    
    @PostMapping("/removeFriend")
    public ResponseEntity<String> removeFriend(@RequestParam("friendId") int friendId) {
        friendService.removeFriend(AuthUtils.getPersonId(), friendId);
        return ResponseEntity.ok("Friend " + peopleService.getOneUser(friendId).getUsername() + " removed successfully.");
    }
}
