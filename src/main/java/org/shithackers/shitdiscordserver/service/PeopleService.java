package org.shithackers.shitdiscordserver.service;

import org.shithackers.shitdiscordserver.model.chat.ChatMessage;
import org.shithackers.shitdiscordserver.model.friend.FriendList;
import org.shithackers.shitdiscordserver.model.friend.FriendRequest;
import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerChannelMessage;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.repo.chat.ChatMessageRepo;
import org.shithackers.shitdiscordserver.repo.chat.ChatRepo;
import org.shithackers.shitdiscordserver.repo.friend.FriendListRepo;
import org.shithackers.shitdiscordserver.repo.friend.FriendRequestRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerChannelMessageRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.shithackers.shitdiscordserver.repo.user.UserRepo;
import org.shithackers.shitdiscordserver.service.server.ServerService;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
public class PeopleService {
    private final UserRepo userRepo;
    private final ChatRepo chatRepo;
    private final ChatMessageRepo chatMessageRepo;
    private final FriendListRepo friendListRepo;
    private final FriendRequestRepo friendRequestRepo;
    private final ServerRepo serverRepo;
    private final ServerService serverService;
    private final FriendService friendService;
    private final ServerMemberRepo serverMemberRepo;
    private final ServerChannelMessageRepo serverChannelMessageRepo;
    
    @Autowired
    public PeopleService(UserRepo userRepo, ChatRepo chatRepo, ChatMessageRepo chatMessageRepo, FriendListRepo friendListRepo, FriendRequestRepo friendRequestRepo, ServerRepo serverRepo, ServerService serverService, FriendService friendService, ServerMemberRepo serverMemberRepo, ServerChannelMessageRepo serverChannelMessageRepo) {
        this.userRepo = userRepo;
        this.chatRepo = chatRepo;
        this.chatMessageRepo = chatMessageRepo;
        this.friendListRepo = friendListRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.serverRepo = serverRepo;
        this.serverService = serverService;
        this.friendService = friendService;
        this.serverMemberRepo = serverMemberRepo;
        this.serverChannelMessageRepo = serverChannelMessageRepo;
    }
    
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
    
    public User getOneUser(int id) {
        return userRepo.findById(id).orElse(null);
    }
    
    public List<Map<String, Object>> getMutualServers(User user) {
        
        List<Server> userServers = serverRepo.findAllByMembersPerson(user);
        List<Server> myServers = serverRepo.findAllByMembersPerson(AuthUtils.getPerson());
        
        List<Server> mutualServers = new ArrayList<>(myServers.stream().filter(userServers::contains).toList());
        List<Map<String, Object>> list = new ArrayList<>();
        for (Server server : mutualServers) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", server.getId());
            map.put("name", server.getName());
            map.put("friendServerUsername", serverMemberRepo.findByPerson(user).getServerUsername());
            list.add(map);
        }
        return list;
    }
    
    public List<User> getMutualFriends(User user) throws SQLException {
        List<User> userFriends = friendService.getFriendList(user);
        List<User> myFriends = friendService.getFriendList(Objects.requireNonNull(AuthUtils.getPerson()));
        List<User> mutualFriends = new ArrayList<>();
        
        for (User friend : myFriends) {
            if (userFriends.contains(friend)) {
                mutualFriends.add(friend);
            }
        }
        return mutualFriends;
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
}
