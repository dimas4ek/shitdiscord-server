package org.shithackers.shitdiscordserver.websocket.controller;

import org.shithackers.shitdiscordserver.model.chat.ChatMessage;
import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerChannel;
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
    
    @GetMapping("/channels/me/{channelId}/messages")
    public Map<String, Object> showRoomMessages(@PathVariable int channelId) {
        Map<String, Object> map = new HashMap<>();
        map.put("messages", chatService.getChatById(channelId).getMessages());
        return map;
    }
    
    @PostMapping("/channels/me/{channelId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ChatMessage> postRoomMessage(@PathVariable int channelId, @RequestParam String message) {
       return new ResponseEntity<>(chatMessageService.sendMessageRest(channelId, AuthUtils.getPerson(), message), HttpStatus.CREATED);
    }
    
    @PostMapping("/channels/{serverId}/{serverChannelId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ServerChannelMessage>
    postServerChannelMessage(@PathVariable int serverId,
                             @PathVariable int serverChannelId,
                             @RequestParam String message) {
        return new ResponseEntity<>(serverChannelService.sendMessageRest(serverId, serverChannelId, AuthUtils.getPerson(), message), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/channels/{serverId}/{serverChannelId}/{messageId}")
    public ResponseEntity<?> deleteServerChannelMessage(@PathVariable int serverId, @PathVariable int serverChannelId, @PathVariable int messageId) {
        serverChannelService.deleteServerChannelMessage(serverId, serverChannelId, messageId);
        
        return ResponseEntity.ok("Message deleted");
    }
    
    @GetMapping("/channels/{serverId}/{serverChannelId}/messages")
    @ResponseBody
    public Map<String, Object> serverChannelMessages(@PathVariable int serverId, @PathVariable int serverChannelId) {
        Server server = serverService.getServer(serverId);
        ServerChannel serverChannel = serverChannelService.getServerChannel(server, serverChannelId);
        
        Map<String, Object> map = new HashMap<>();
        map.put("messages", serverChannel.getServerChannelMessages());

        return map;
    }
}
