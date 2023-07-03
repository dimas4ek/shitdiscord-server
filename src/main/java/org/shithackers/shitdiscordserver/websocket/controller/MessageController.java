package org.shithackers.shitdiscordserver.websocket.controller;

import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    /*private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final PeopleService peopleService;
    private final ServerChannelService serverChannelService;

    @Autowired
    public MessageController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService, PeopleService peopleService, ServerChannelService serverChannelService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
        this.peopleService = peopleService;
        this.serverChannelService = serverChannelService;
    }

    @MessageMapping("/channels/me/{channelId}")
    @SendTo("/topic/channels/me/{channelId}")
    public WSMessage wsMessage(SimpMessageHeaderAccessor headerAccessor, @DestinationVariable int channelId, ChatMessage message) {
        User sender = peopleService.getOneUser(Objects.requireNonNull(headerAccessor.getUser()).getName());
        chatMessageService.sendMessage(channelId, sender, message.getMessage());
        return new WSMessage(
            channelId,
            headerAccessor.getUser().getName(),
            message.getMessage(),
            new Date()
        );
    }

    @MessageMapping("/channels/{serverId}/{serverChannelId}")
    @SendTo("/topic/channels/{serverId}/{serverChannelId}")
    public WSServerChannelMessage wsServerChannelMessage(SimpMessageHeaderAccessor headerAccessor,
                                                         @DestinationVariable int serverId,
                                                         @DestinationVariable int serverChannelId,
                                                         ServerChannelMessage message) {
        User sender = peopleService.getOneUser(Objects.requireNonNull(headerAccessor.getUser()).getName());
        serverChannelService.sendMessage(serverId, serverChannelId, sender, message.getMessage());
        return new WSServerChannelMessage(
            serverId,
            serverChannelId,
            headerAccessor.getUser().getName(),
            message.getMessage(),
            new Date()
        );
    }

    @GetMapping("/channels/me/{channelId}")
    public String showRoomMessages(@PathVariable String channelId, Model model) {
        model.addAttribute("channelId", channelId);
        return "index";
    }

    @PostMapping("/channels/me/{channelId}")
    public String postRoomMessage(SimpMessageHeaderAccessor headerAccessor, @PathVariable int channelId, @RequestParam String message) {
        messagingTemplate.convertAndSend(
            "/topic/channels/me/" + channelId,
            new WSMessage(
                channelId,
                Objects.requireNonNull(headerAccessor.getUser()).getName(),
                message,
                new Date()
            )
        );
        return "redirect:/channels/me/" + channelId;
    }

    @PostMapping("/channels/{serverId}/{serverChannelId}")
    public String postServerChannelMessage(SimpMessageHeaderAccessor headerAccessor,
                                           @PathVariable int serverId, @PathVariable int serverChannelId,
                                           @RequestParam String message
    ) {
        messagingTemplate.convertAndSend(
            "/topic/channels/" + serverId + "/" + serverChannelId,
            new WSServerChannelMessage(
                serverId,
                serverChannelId,
                Objects.requireNonNull(headerAccessor.getUser()).getName(),
                message,
                new Date()
            )
        );
        return "redirect:/channels/" + serverId + "/" + serverChannelId;
    }

    @GetMapping("/channels/me/{channelId}/messages")
    @ResponseBody
    public List<WSMessage> messages(@PathVariable int channelId) {
        return chatMessageService.getChatMessages(channelId);
    }

    @GetMapping("/channels/{serverId}/{serverChannelId}/messages")
    @ResponseBody
    public List<WSServerChannelMessage> serverChannelMessages(@PathVariable int serverId, @PathVariable int serverChannelId) {
        return serverChannelService.getServerChannelMessages(serverId, serverChannelId);
    }*/
}
