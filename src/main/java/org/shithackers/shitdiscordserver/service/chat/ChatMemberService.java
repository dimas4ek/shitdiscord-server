package org.shithackers.shitdiscordserver.service.chat;

import org.shithackers.shitdiscordserver.model.chat.ChatMember;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.repo.chat.ChatMemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMemberService {
    private final ChatMemberRepo chatMemberRepo;
    
    @Autowired
    public ChatMemberService(ChatMemberRepo chatMemberRepo) {
        this.chatMemberRepo = chatMemberRepo;
    }
    
    
    public ChatMember getChatMemberByUsername(String selectedUser) {
        return chatMemberRepo.findByPersonUsername(selectedUser);
    }
    
    public ChatMember getChatMemberByPerson(User person) {
        return chatMemberRepo.findByPerson(person);
    }
    
    public List<ChatMember> getChatMembersByPerson(User person) {
        return chatMemberRepo.findAllByPerson(person);
    }
    
    public ChatMember getChatMemberByPersonId(int personId) {
        return chatMemberRepo.findByPersonId(personId);
    }
}
