package org.shithackers.shitdiscordserver.controller;

import org.shithackers.shitdiscordserver.model.chat.Chat;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.service.FriendService;
import org.shithackers.shitdiscordserver.service.PeopleService;
import org.shithackers.shitdiscordserver.service.chat.ChatService;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class MainController {
    private final FriendService friendService;
    private final PeopleService peopleService;
    private final ChatService chatService;

    @Autowired
    public MainController(FriendService friendService, PeopleService peopleService, ChatService chatListService) {
        this.friendService = friendService;
        this.peopleService = peopleService;
        this.chatService = chatListService;
    }
    
    @GetMapping("/user")
    public User currentUser() {
        return AuthUtils.getPerson();
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
    
    @GetMapping("/chats")
    public List<Chat> getChats() {
        return chatService.getChannelList();
    }
    
    @GetMapping("/chats/{chatId}")
    public Map<String, Object> getChat(@PathVariable("chatId") int chatId) {
        Chat chat = chatService.getChatById(chatId);
        Map<String, Object> map = new HashMap<>();
        map.put("chat", chat);
        
        return map;
    }

    @PostMapping(value = "/createChat", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Chat> createChat(@RequestBody List<User> selectedUsers) {
        List<String> selectedUsersString = new ArrayList<>();
        for (User user : selectedUsers) {
            selectedUsersString.add(user.getUsername());
        }
        if (selectedUsers.size() == 1) {
            System.out.println(selectedUsers.get(0).getUsername());
            if (chatService.chatExists(selectedUsers.get(0))) {
                String chatId = chatService.createdChat(selectedUsers.get(0));
                if (chatId != null) {
                    Chat chat = chatService.getChatById(Integer.parseInt(chatId));
                    return ResponseEntity.ok(chat);
                }
            }
        }

        return new ResponseEntity<>(chatService.createGroupChatRest(selectedUsersString), HttpStatus.CREATED);
    }
}
