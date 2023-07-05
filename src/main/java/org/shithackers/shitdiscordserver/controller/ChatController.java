package org.shithackers.shitdiscordserver.controller;

import org.shithackers.shitdiscordserver.model.chat.Chat;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.service.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatListService) {
        this.chatService = chatListService;
    }
    
    @GetMapping("/chats")
    public List<Map<String, Object>> getChats() {
        return chatService.getChats();
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
