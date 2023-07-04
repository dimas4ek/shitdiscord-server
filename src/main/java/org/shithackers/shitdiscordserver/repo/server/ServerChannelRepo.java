package org.shithackers.shitdiscordserver.repo.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerChannelRepo extends JpaRepository<ServerChannel, Integer> {
    ServerChannel findByServerAndId(Server server, int channelId);
    List<ServerChannel> findAllByServer(Server server);
}
