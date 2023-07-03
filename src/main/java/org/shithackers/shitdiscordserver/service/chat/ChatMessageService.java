package org.shithackers.shitdiscordserver.service.chat;

import org.shithackers.shitdiscordserver.model.chat.ChatMessage;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.repo.chat.ChatMessageRepo;
import org.shithackers.shitdiscordserver.repo.chat.ChatRepo;
import org.shithackers.shitdiscordserver.websocket.model.WSMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ChatMessageService {
    private final ChatMessageRepo chatMessageRepo;
    private final ChatRepo chatRepo;

    @Autowired
    public ChatMessageService(ChatMessageRepo chatMessageRepo, ChatRepo chatRepo) {
        this.chatMessageRepo = chatMessageRepo;
        this.chatRepo = chatRepo;
    }

    @Transactional
    public void sendMessage(int channelId, User sender, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chatRepo.findById(channelId).orElse(null));
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setCreatedAt(new Date());

        chatMessageRepo.save(chatMessage);
    }
    
    @Transactional
    public ChatMessage sendMessageRest(int channelId, User sender, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChat(chatRepo.findById(channelId).orElse(null));
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setCreatedAt(new Date());
        
        chatMessageRepo.save(chatMessage);
        
        return chatMessage;
    }

    public List<WSMessage> getChatMessages(int id) {
        List<WSMessage> wsMessages = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessageRepo.findAllByChatId(id)) {
            WSMessage wsMessage = new WSMessage(
                chatMessage.getChat().getId(),
                chatMessage.getSender().getUsername(),
                chatMessage.getMessage(),
                chatMessage.getCreatedAt()
            );
            wsMessages.add(wsMessage);
        }
        return wsMessages;
    }

}
