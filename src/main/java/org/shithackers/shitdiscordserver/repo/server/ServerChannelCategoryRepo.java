package org.shithackers.shitdiscordserver.repo.server;

import org.shithackers.shitdiscordserver.model.server.ServerChannelCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerChannelCategoryRepo extends JpaRepository<ServerChannelCategory, Integer> {
    List<ServerChannelCategory> findAllByServerId(int serverId);
}
