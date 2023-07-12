package org.shithackers.shitdiscordserver.service;

import org.shithackers.shitdiscordserver.model.friend.FriendList;
import org.shithackers.shitdiscordserver.model.friend.FriendRequest;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.repo.friend.FriendListRepo;
import org.shithackers.shitdiscordserver.repo.friend.FriendRequestRepo;
import org.shithackers.shitdiscordserver.repo.user.UserRepo;
import org.shithackers.shitdiscordserver.service.chat.ChatService;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FriendService {
    private final FriendListRepo friendListRepo;
    private final FriendRequestRepo friendRequestRepo;
    private final UserRepo peopleRepo;
    private final ChatService chatService;
    
    @Autowired
    public FriendService(FriendListRepo friendRepo, FriendRequestRepo friendRequestRepo, UserRepo peopleRepo, ChatService chatService) {
        this.friendListRepo = friendRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.peopleRepo = peopleRepo;
        this.chatService = chatService;
    }
    
    public List<User> getFriendList(User user) throws SQLException {
        List<User> friends = new ArrayList<>();
        for (FriendList friend : friendListRepo.findAllByPersonId(user.getId())) {
            friends.add(peopleRepo.findById(friend.getFriend().getId()).orElse(null));
        }
        
        return friends;
    }
    
    @Transactional
    public void addFriend(User person, User friend) {
        addToFriend(friend, person);
        addToFriend(person, friend);
        chatService.createChat(person, friend);
    }
    
    private void addToFriend(User person, User friend) {
        FriendList friendList = new FriendList();
        friendList.setPerson(person);
        friendList.setFriend(friend);
        friendList.setCreatedAt(new Date());
        
        friendListRepo.save(friendList);
    }
    
    public boolean isFriend(String friendName) {
        return
            friendListRepo.findByPersonAndFriend(
                AuthUtils.getPerson(),
                peopleRepo.findByUsername(friendName).orElse(null)
            ) != null;
    }
    
    @Transactional
    public void removeFriend(int personId, int friendId) {
        removeFromFriend(personId, friendId);
        removeFromFriend(friendId, personId);
    }
    
    @Transactional
    public void removeFromFriend(int personId, int friendId) {
        FriendList friendList = friendListRepo.findByPersonAndFriend(
            peopleRepo.findById(personId).orElse(null),
            peopleRepo.findById(friendId).orElse(null)
        );
        if (friendList != null) {
            friendListRepo.delete(friendList);
        }
    }
    
    @Transactional
    public void friendRequest(String friendName) {
        FriendRequest request = new FriendRequest();
        request.setSender(peopleRepo.findById(AuthUtils.getPersonId()).orElse(null));
        request.setReceiver(peopleRepo.findByUsername(friendName).orElse(null));
        request.setStatus(FriendRequest.FriendRequestStatus.PENDING.name());
        request.setCreatedAt(new Date());
        
        friendRequestRepo.save(request);
    }
    
    @Transactional
    public void acceptFriendRequest(int requestId) {
        FriendRequest request = friendRequestRepo.findById(requestId).orElse(null);
        if (request != null) {
            request.setStatus(FriendRequest.FriendRequestStatus.ACCEPTED.name());
            friendRequestRepo.save(request);
            addFriend(request.getSender(), request.getReceiver());
        }
    }
    
    @Transactional
    public void declineFriendRequest(int requestId) {
        FriendRequest request = friendRequestRepo.findById(requestId).orElse(null);
        if (request != null) {
            request.setStatus(FriendRequest.FriendRequestStatus.DECLINED.name());
            friendRequestRepo.save(request);
        }
    }
    
    public boolean alreadyFriendRequest(String friendName) {
        List<FriendRequest> request =
            friendRequestRepo.findAllBySenderAndReceiver(
                AuthUtils.getPerson(),
                peopleRepo.findByUsername(friendName).orElse(null)
            );
        for (FriendRequest friendRequest : request) {
            if (friendRequest.getStatus().equals(FriendRequest.FriendRequestStatus.PENDING.name())) {
                return true;
            }
        }
        return false;
    }
    
    public void cancelFriendRequest(int requestId) {
        friendRequestRepo.findById(requestId).ifPresent(friendRequestRepo::delete);
    }
    
    public List<FriendRequest> getOutgoingPendingFriendRequestsList() {
        List<FriendRequest> requests = new ArrayList<>();
        for (FriendRequest request : friendRequestRepo.findAllBySender(AuthUtils.getPerson())) {
            if (request.getStatus().equals(FriendRequest.FriendRequestStatus.PENDING.name())) {
                requests.add(request);
            }
        }
        return requests;
    }
    
    public List<FriendRequest> getIncomingPendingFriendRequestsList() {
        List<FriendRequest> requests = new ArrayList<>();
        for (FriendRequest request : friendRequestRepo.findAllByReceiver(AuthUtils.getPerson())) {
            if (request.getStatus().equals(FriendRequest.FriendRequestStatus.PENDING.name())) {
                requests.add(request);
            }
        }
        return requests;
    }
    
    public String requestFromLoggedUser(int requestId) {
        FriendRequest request = friendRequestRepo.findById(requestId).orElse(null);
        if (request == null) {
            return "null";
        }
        return request.getSender() == AuthUtils.getPerson()
               ? "sender" : "receiver";
    }
}

