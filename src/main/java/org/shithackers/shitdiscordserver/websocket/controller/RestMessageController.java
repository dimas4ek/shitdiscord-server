package org.shithackers.shitdiscordserver.websocket.controller;

import org.shithackers.shitdiscordserver.model.chat.ChatMessage;
import org.shithackers.shitdiscordserver.model.server.ServerChannelMessage;
import org.shithackers.shitdiscordserver.service.chat.ChatMessageService;
import org.shithackers.shitdiscordserver.service.chat.ChatService;
import org.shithackers.shitdiscordserver.service.server.ServerChannelService;
import org.shithackers.shitdiscordserver.service.server.ServerService;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RestMessageController {
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;
    private final ServerService serverService;
    private final ServerChannelService serverChannelService;
    
    @Autowired
    public RestMessageController(ChatService chatService, ChatMessageService chatMessageService, ServerService serverService, ServerChannelService serverChannelService) {
        this.chatService = chatService;
        this.chatMessageService = chatMessageService;
        this.serverService = serverService;
        this.serverChannelService = serverChannelService;
    }
    
    @GetMapping("/chats/{chatId}/messages")
    public Map<String, Object> showRoomMessages(@PathVariable int chatId) {
        Map<String, Object> map = new HashMap<>();
        map.put("messages", chatService.getChatById(chatId).getMessages());
        return map;
    }
    
    @PostMapping("/chats/{chatId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChatMessage> postRoomMessage(@PathVariable int chatId, @RequestParam String message) {
       return new ResponseEntity<>(chatMessageService.sendMessageRest(chatId, AuthUtils.getPerson(), message), HttpStatus.CREATED);
    }
    
    @PostMapping("/servers/{serverId}/{serverChannelId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ServerChannelMessage>
    postServerChannelMessage(@PathVariable int serverId,
                             @PathVariable int serverChannelId,
                             @RequestBody String message) {
        return new ResponseEntity<>(serverChannelService.sendMessageRest(serverId, serverChannelId, message), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/servers/{serverId}/{serverChannelId}/{messageId}")
    public ResponseEntity<?> deleteServerChannelMessage(@PathVariable int serverId, @PathVariable int serverChannelId, @PathVariable int messageId) {
        serverChannelService.deleteServerChannelMessage(serverId, serverChannelId, messageId);
        
        return ResponseEntity.ok("Message deleted");
    }
    
    @GetMapping("/servers/{serverId}/{serverChannelId}/messages")
    @ResponseBody
    public List<Map<String, Object>> serverChannelMessages(@PathVariable int serverId, @PathVariable int serverChannelId) {
        return serverChannelService.getServerChannelMessagesRest(serverId, serverChannelId);
    }
}
