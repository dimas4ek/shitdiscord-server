package org.shithackers.shitdiscordserver.repo.friend;

import org.shithackers.shitdiscordserver.model.friend.FriendRequest;
import org.shithackers.shitdiscordserver.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepo extends JpaRepository<FriendRequest, Integer> {
    List<FriendRequest> findAllByReceiver(User receiver);
    List<FriendRequest> findAllBySenderAndReceiver(User sender, User receiver);
    List<FriendRequest> findAllBySender(User person);
    List<FriendRequest> findAllBySenderIdOrReceiverId(int senderId, int receiverId);
}
