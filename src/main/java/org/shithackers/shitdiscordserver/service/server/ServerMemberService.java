package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServerMemberService {
    private final ServerRepo serverRepo;
    private final ServerMemberRepo serverMemberRepo;
    private final ServerRoleService serverRoleService;
    
    @Autowired
    public ServerMemberService(ServerRepo serverRepo, ServerMemberRepo serverMemberRepo, ServerRoleService serverRoleService) {
        this.serverRepo = serverRepo;
        this.serverMemberRepo = serverMemberRepo;
        this.serverRoleService = serverRoleService;
    }
    
    public List<Map<String, Object>> getServerMembers(int serverId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<ServerMember> members = serverMemberRepo.findAllByServerId(serverId);
        for (ServerMember member : members) {
            list.add(getServerMemberInfo(serverId, member.getId()));
        }
        return list;
    }
    
    public Map<String, Object> getServerMemberInfo(int serverId, int memberId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerMember member = serverMemberRepo.findByServerIdAndId(serverId, memberId);
        if (server != null && member != null) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", member.getId());
            map.put("user", member.getPerson());
            map.put("roles", serverRoleService.getMemberRoles(serverId, memberId));
            map.put("joinDate", member.getJoinDate());
            return map;
        }
        return null;
    }
    
    @Transactional
    public void changeUsername(int serverId, int memberId, String newUsername) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerMember serverMember = serverMemberRepo.findByServerIdAndId(serverId, memberId);
        if (server != null && serverMember != null) {
            serverMember.setServerUsername(newUsername);
            serverMemberRepo.save(serverMember);
        }
    }
}
