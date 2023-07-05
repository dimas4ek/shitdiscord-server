package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerMemberService {
    private final ServerRepo serverRepo;
    private final ServerMemberRepo serverMemberRepo;
    
    @Autowired
    public ServerMemberService(ServerRepo serverRepo, ServerMemberRepo serverMemberRepo) {
        this.serverRepo = serverRepo;
        this.serverMemberRepo = serverMemberRepo;
    }
    
    public List<ServerMember> getServerMembers(int serverId) {
        return serverMemberRepo.findAllByServerId(serverId);
    }
    
    public ServerMember getServerMember(int serverId, int memberId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            return serverMemberRepo.findById(memberId).orElse(null);
        }
        return null;
    }
}
