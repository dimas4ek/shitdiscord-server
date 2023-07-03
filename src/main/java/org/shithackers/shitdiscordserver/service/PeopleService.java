package org.shithackers.shitdiscordserver.service;

import org.shithackers.shitdiscordserver.model.chat.ChatMessage;
import org.shithackers.shitdiscordserver.model.friend.FriendList;
import org.shithackers.shitdiscordserver.model.friend.FriendRequest;
import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerChannelMessage;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.repo.chat.ChatMessageRepo;
import org.shithackers.shitdiscordserver.repo.friend.FriendListRepo;
import org.shithackers.shitdiscordserver.repo.friend.FriendRequestRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerChannelMessageRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.shithackers.shitdiscordserver.repo.user.RoleRepo;
import org.shithackers.shitdiscordserver.repo.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeopleService {
    private final UserRepo userRepo;
    private final ChatMessageRepo chatMessageRepo;
    private final FriendListRepo friendListRepo;
    private final FriendRequestRepo friendRequestRepo;
    private final ServerRepo serverRepo;
    private final ServerMemberRepo serverMemberRepo;
    private final ServerChannelMessageRepo serverChannelMessageRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder encoder;
    
    @Autowired
    public PeopleService(UserRepo userRepo, ChatMessageRepo chatMessageRepo, FriendListRepo friendListRepo, FriendRequestRepo friendRequestRepo, ServerRepo serverRepo, ServerMemberRepo serverMemberRepo, ServerChannelMessageRepo serverChannelMessageRepo, RoleRepo roleRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.friendListRepo = friendListRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.serverRepo = serverRepo;
        this.serverMemberRepo = serverMemberRepo;
        this.serverChannelMessageRepo = serverChannelMessageRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }
    
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
    public User getOneUser(int id) {
        return userRepo.findById(id).orElse(null);
    }
    
    @Transactional
    public void edit(int id, User user) {
        user.setId(id);
        
        userRepo.save(user);
    }
    
    @Transactional
    public User editRest(int id, User user) {
        user.setId(id);
        user.setPassword(user.getPassword());
        userRepo.save(user);
        
        return user;
    }
    
    public void delete(int personId) {
        List<FriendList> friendLists = friendListRepo.findAllByPersonIdOrFriendId(personId, personId);
        friendListRepo.deleteAll(friendLists);
        
        List<FriendRequest> friendRequests = friendRequestRepo.findAllBySenderIdOrReceiverId(personId, personId);
        friendRequestRepo.deleteAll(friendRequests);
        
        List<ServerMember> serverMemberList = serverMemberRepo.findAllByPersonId(personId);
        serverMemberRepo.deleteAll(serverMemberList);
        
        List<Server> server = serverRepo.findAllByCreatorId(personId);
        //доделать
        //поместить в удаленные сервера или просто удалить
        List<ChatMessage> chatMessages = chatMessageRepo.findAllBySenderId(personId);
        List<ServerChannelMessage> serverChannelMessages = serverChannelMessageRepo.findAllBySenderId(personId);
        //доделать
        //создать таблицу с удаленными юзерами
        //поместить туда юзера
        //заменить отправителя сообщений
        userRepo.deleteById(personId);
    }
    
    public User getOneUser(String username) {
        return userRepo.findByUsername(username).get();
    }
    
    public boolean isExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }
}
