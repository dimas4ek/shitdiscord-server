package org.shithackers.shitdiscordserver.repo.server;

import org.shithackers.shitdiscordserver.model.server.BannedServerMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannedServerMemberRepo extends JpaRepository<BannedServerMember, Integer> {
    BannedServerMember findByServerIdAndId(int serverId, int serverMemberId);
    
    List<BannedServerMember> findAllByServerId(int serverId);
}
