package org.shithackers.shitdiscordserver.repo.server;

import org.shithackers.shitdiscordserver.model.server.ServerRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRoleRepo extends JpaRepository<ServerRole, Integer> {
    List<ServerRole> findAllByServerId(int serverId);
    ServerRole findByServerIdAndId(int serverId, int id);
    
    List<ServerRole> findAllByServerIdAndMembersId(int serverId, int memberId);
}
