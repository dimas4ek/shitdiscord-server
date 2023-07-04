package org.shithackers.shitdiscordserver.service.chat;

import org.shithackers.shitdiscordserver.model.chat.Chat;
import org.shithackers.shitdiscordserver.model.chat.ChatMember;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.repo.chat.ChatMemberRepo;
import org.shithackers.shitdiscordserver.repo.chat.ChatRepo;
import org.shithackers.shitdiscordserver.repo.user.UserRepo;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ChatService {
    private final ChatRepo chatRepo;
    private final UserRepo peopleRepo;
    private final ChatMemberService chatMemberService;
    private final ChatMemberRepo chatMemberRepo;
    
    @Autowired
    public ChatService(ChatRepo chatRepo, UserRepo peopleRepo, ChatMemberService chatMemberService, ChatMemberRepo chatMemberRepo) {
        this.chatRepo = chatRepo;
        this.peopleRepo = peopleRepo;
        this.chatMemberService = chatMemberService;
        this.chatMemberRepo = chatMemberRepo;
    }
    
    public List<Chat> getChannelList() {
        return chatRepo.findAllByMembersIn(
            Collections.singleton(
                List.of(chatMemberService.getChatMemberByPerson(AuthUtils.getPerson()))
            )
        );
    }
    
    public boolean chatExists(User selectedUser) {
        List<Chat> chats = chatRepo.findAll();
        for (Chat chat : chats) {
            if (chat.getMembers().size() == 2 && !chat.isGroup()) {
                User person1 = chat.getMembers().get(0).getPerson();
                User person2 = chat.getMembers().get(1).getPerson();
                if (
                    person1.getUsername().equals(AuthUtils.getPerson().getUsername()) &&
                        person2.getUsername().equals(selectedUser.getUsername())
                        ||
                        person2.getUsername().equals(AuthUtils.getPerson().getUsername()) &&
                            person1.getUsername().equals(selectedUser.getUsername())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String createdChat(User selectedUser) {
        List<Chat> chats = chatRepo.findAll();
        for (Chat chat : chats) {
            if (chat.getMembers().size() == 2 && !chat.isGroup()) {
                User person1 = chat.getMembers().get(0).getPerson();
                User person2 = chat.getMembers().get(1).getPerson();
                if (
                    person1.getUsername().equals(AuthUtils.getPerson().getUsername()) &&
                        person2.getUsername().equals(selectedUser.getUsername())
                        ||
                        person2.getUsername().equals(AuthUtils.getPerson().getUsername()) &&
                            person1.getUsername().equals(selectedUser.getUsername())) {
                    return String.valueOf(chat.getId());
                } else {
                    return null;
                }
            }
        }
        return null;
    }
    
    @Transactional
    public void createChat(User friend1, User friend2) {
        Chat chat = new Chat();
        chat.setGroup(false);
        chat.setCreatedAt(new Date());
        
        chatRepo.save(chat);
        
        saveChatMembers(List.of(friend1, friend2), chat);
    }
    
    @Transactional
    public Chat createGroupChatRest(List<String> selectedUsers) {
        Chat chat = new Chat();
        List<User> members = new ArrayList<>();
        members.add(AuthUtils.getPerson());
        for (String selectedUser : selectedUsers) {
            members.add(peopleRepo.findByUsername(selectedUser).orElse(null));
        }
        
        chat.setGroup(true);
        chat.setCreatedAt(new Date());
        
        chatRepo.save(chat);
        
        saveChatMembers(members, chat);
        
        return chat;
    }
    
    @Transactional
    public void saveChatMembers(List<User> people, Chat chat) {
        for (User person : people) {
            ChatMember member = new ChatMember();
            member.setChat(chat);
            member.setPerson(person);
            chatMemberRepo.save(member);
        }
    }
    
    public Chat getChatById(int chatId) {
        return chatRepo.findById(chatId).orElse(null);
    }
}
