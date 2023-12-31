package org.shithackers.shitdiscordserver.repo.chat;

import org.shithackers.shitdiscordserver.model.chat.Chat;
import org.shithackers.shitdiscordserver.model.chat.ChatMember;
import org.shithackers.shitdiscordserver.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Integer> {
    Chat findByMembersInAndIsGroupFalse(Set<List<ChatMember>> members);
    List<Chat> findAllByMembersPerson(User user);
}
