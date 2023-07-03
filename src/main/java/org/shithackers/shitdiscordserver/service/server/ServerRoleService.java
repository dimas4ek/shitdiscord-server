package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.model.server.ServerRole;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServerRoleService {
    private final ServerRoleRepo serverRoleRepo;
    private final ServerRepo serverRepo;
    private final ServerMemberRepo serverMemberRepo;
    
    @Autowired
    public ServerRoleService(ServerRoleRepo serverRoleRepo, ServerRepo serverRepo, ServerMemberRepo serverMemberRepo) {
        this.serverRoleRepo = serverRoleRepo;
        this.serverRepo = serverRepo;
        this.serverMemberRepo = serverMemberRepo;
    }
    
    public List<ServerRole> getServerRoles(int serverId) {
        return serverRoleRepo.findAllByServerId(serverId);
    }
    
    @Transactional
    public ServerRole createRole(int serverId, ServerRole serverRole) {
        serverRole.setServer(serverRepo.findById(serverId).orElse(null));
        serverRoleRepo.save(serverRole);
        
        return serverRole;
    }
    
    public ServerRole showRole(int serverId, int roleId) {
        return serverRoleRepo.findByServerIdAndId(serverId, roleId);
    }
    
    @Transactional
    public void addRoleToMember(int serverId, int serverMemberId, int roleId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if(server != null) {
            ServerMember member = serverMemberRepo.findById(serverMemberId).orElse(null);
            ServerRole role = serverRoleRepo.findByServerIdAndId(serverId, roleId);
            
            if (member != null && role != null) {
                if (!member.getRoles().contains(role)) {
                    member.getRoles().add(role);
                    serverMemberRepo.save(member);
                }
            }
        }
    }
    
    
    public Map<String, Object> showMemberRoles(int serverId, int serverMemberId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            ServerMember member = serverMemberRepo.findById(serverMemberId).orElse(null);
            if (member != null) {
                Map<String, Object> memberMap = new HashMap<>();
                memberMap.put("user", member.getPerson());
                memberMap.put("serverRoles", member.getRoles());
                Map<String, Object> result = new HashMap<>();
                result.put("serverId", serverId);
                result.put("member", memberMap);
                
                return result;
            }
            
            return Map.of(
                "serverId", serverId,
                "member", "null"
            );
        }
        
        return Map.of("server", "null");
    }
    
    @Transactional
    public void removeRoleFromMember(int serverId, int serverMemberId, int roleId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            ServerMember member = serverMemberRepo.findById(serverMemberId).orElse(null);
            ServerRole role = serverRoleRepo.findByServerIdAndId(serverId, roleId);
            
            if (member != null && role != null) {
                if (member.getRoles().contains(role)) {
                    member.getRoles().remove(role);
                    serverMemberRepo.save(member);
                }
            }
        }
    }
}
