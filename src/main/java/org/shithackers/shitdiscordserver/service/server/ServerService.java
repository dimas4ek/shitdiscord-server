package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ServerService {
    private final ServerRepo serverRepo;
    private final ServerChannelService serverChannelService;
    private final ServerMemberRepo serverMemberRepo;
    private final ServerRoleService serverRoleService;
    
    @Autowired
    public ServerService(ServerRepo serverRepo, ServerChannelService serverChannelService, ServerMemberRepo serverMemberRepo, ServerRoleService serverRoleService) {
        this.serverRepo = serverRepo;
        this.serverChannelService = serverChannelService;
        this.serverMemberRepo = serverMemberRepo;
        this.serverRoleService = serverRoleService;
    }
    
    public Server getServer(int serverId) {
        return serverRepo.findById(serverId).orElse(null);
    }
    
    public Map<String, Object> getServerInfo(int serverId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            Map<String, Object> serverMap = new LinkedHashMap<>();
            serverMap.put("id", server.getId());
            serverMap.put("name", server.getName());
            serverMap.put("creator", server.getCreator());
            serverMap.put("members", serverMemberRepo.findAllByServerId(serverId));
            serverMap.put("channels", serverChannelService.getServerChannels(serverId));
            serverMap.put("roles", serverRoleService.getServerRoles(serverId));
            serverMap.put("createdAt", server.getCreatedAt());
            return serverMap;
        }
        return null;
    }
    
    @Transactional
    public Server saveRest(Server server) {
        server.setCreator(AuthUtils.getPerson());
        server.setCreatedAt(new Date());
        serverRepo.save(server);
        
        serverChannelService.createFirstServerChannel(server);
        
        ServerMember serverMember = new ServerMember();
        serverMember.setServer(server);
        serverMember.setPerson(AuthUtils.getPerson());
        serverMember.setJoinDate(new Date());
        serverMemberRepo.save(serverMember);
        
        return server;
    }
    
    public List<Map<String, Object>> getServerList() {
        List<Server> servers = serverRepo.findAllByMembersPerson(AuthUtils.getPerson());
        List<Map<String, Object>> serverList = new ArrayList<>();
        
        if(servers != null) {
            servers.forEach(server -> {
                Map<String, Object> serverMap = new LinkedHashMap<>();
                serverMap.put("id", server.getId());
                serverMap.put("name", server.getName());
                
                serverList.add(serverMap);
            });
            
            return serverList;
        }
        
        return null;
    }
    
    
    public List<ServerMember> getServerMemberList(Server server) {
        return serverMemberRepo.findAllByServerId(server.getId());
    }
    
    public void deleteServer(Server server) {
        serverRepo.delete(server);
    }
    
    public void joinServer(int serverId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            ServerMember serverMember = new ServerMember();
            serverMember.setServer(server);
            serverMember.setPerson(AuthUtils.getPerson());
            serverMember.setJoinDate(new Date());
            serverMemberRepo.save(serverMember);
        }
    }
    
    @Transactional
    public boolean kickFromServer(int serverId, int memberId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerMember member = serverMemberRepo.findById(memberId).orElse(null);
        if (server != null && member != null) {
            serverMemberRepo.delete(member);
            return true;
        }
        return false;
    }
}
