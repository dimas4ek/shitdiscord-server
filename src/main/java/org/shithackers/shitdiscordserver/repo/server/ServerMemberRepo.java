package org.shithackers.shitdiscordserver.repo.server;

import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerMemberRepo extends JpaRepository<ServerMember, Integer> {
    List<ServerMember> findAllByServerId(int serverId);
    List<ServerMember> findAllByPersonId(int personId);
}
