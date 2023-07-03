package org.shithackers.shitdiscordserver.repo.chat;

import org.shithackers.shitdiscordserver.model.chat.ChatMember;
import org.shithackers.shitdiscordserver.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepo extends JpaRepository<ChatMember, Integer> {
    ChatMember findByPersonUsername(String username);
    ChatMember findByPerson(User person);
    List<ChatMember> findAllByPerson(User person);
    ChatMember findByPersonId(int personId);
}
