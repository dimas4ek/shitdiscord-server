package org.shithackers.shitdiscordserver.repo.chat;

import org.shithackers.shitdiscordserver.model.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findAllByChatId(int chatId);
    List<ChatMessage> findAllBySenderId(int senderId);
}
