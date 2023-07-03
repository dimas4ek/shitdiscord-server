package org.shithackers.shitdiscordserver.repo.server;

import org.shithackers.shitdiscordserver.model.server.ServerChannelMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerChannelMessageRepo extends JpaRepository<ServerChannelMessage, Integer> {
    List<ServerChannelMessage> findAllByServerIdAndServerChannelId(int serverId, int channelId);
    List<ServerChannelMessage> findAllBySenderId(int senderId);
}
