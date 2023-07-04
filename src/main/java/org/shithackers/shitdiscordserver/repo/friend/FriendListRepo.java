package org.shithackers.shitdiscordserver.repo.friend;

import org.shithackers.shitdiscordserver.model.friend.FriendList;
import org.shithackers.shitdiscordserver.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendListRepo extends JpaRepository<FriendList, Integer> {
    List<FriendList> findAllByPersonId(int personId);
    FriendList findByPersonAndFriend(User person, User friend);
    List<FriendList> findAllByPersonIdOrFriendId(int personId, int friendId);
}
