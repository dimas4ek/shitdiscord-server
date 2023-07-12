package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.BannedServerMember;
import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.payload.request.ServerBanRequest;
import org.shithackers.shitdiscordserver.repo.server.BannedServerMemberRepo;
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
    private final BannedServerMemberRepo bannedServerMemberRepo;
    
    @Autowired
    public ServerService(ServerRepo serverRepo, ServerChannelService serverChannelService, ServerMemberRepo serverMemberRepo, ServerRoleService serverRoleService, BannedServerMemberRepo bannedServerMemberRepo) {
        this.serverRepo = serverRepo;
        this.serverChannelService = serverChannelService;
        this.serverMemberRepo = serverMemberRepo;
        this.serverRoleService = serverRoleService;
        this.bannedServerMemberRepo = bannedServerMemberRepo;
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
    public Server createServer(Server server) {
        server.setCreator(AuthUtils.getPerson());
        server.setCreatedAt(new Date());
        serverRepo.save(server);
        
        serverChannelService.createFirstServerChannel(server);
        
        ServerMember serverMember = new ServerMember();
        serverMember.setServer(server);
        serverMember.setServerUsername(Objects.requireNonNull(AuthUtils.getPerson()).getUsername());
        serverMember.setPerson(AuthUtils.getPerson());
        serverMember.setJoinDate(new Date());
        serverMemberRepo.save(serverMember);
        
        return server;
    }
    
    public List<Map<String, Object>> getServerList() {
        List<Server> servers = serverRepo.findAllByMembersPerson(AuthUtils.getPerson());
        List<Map<String, Object>> serverList = new ArrayList<>();
        
        if (servers != null) {
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
            serverMember.setServerUsername(Objects.requireNonNull(AuthUtils.getPerson()).getUsername());
            serverMember.setPerson(AuthUtils.getPerson());
            serverMember.setJoinDate(new Date());
            serverMemberRepo.save(serverMember);
        }
    }
    
    @Transactional
    public void kickMember(int serverId, int memberId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerMember member = serverMemberRepo.findById(memberId).orElse(null);
        if (server != null && member != null) {
            serverMemberRepo.delete(member);
        }
    }
    
    @Transactional
    public void banMember(int serverId, int memberId, ServerBanRequest request) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerMember member = serverMemberRepo.findById(memberId).orElse(null);
        if (server != null && member != null) {
            BannedServerMember bannedServerMember = new BannedServerMember();
            bannedServerMember.setUser(member.getPerson());
            bannedServerMember.setBanReason(request.getBanReason());
            bannedServerMember.setServer(server);
            bannedServerMember.setBanDuration(request.getBanDuration());
            bannedServerMember.setBannedAt(new Date());
            
            bannedServerMemberRepo.save(bannedServerMember);
            
            serverMemberRepo.delete(member);
        }
    }
    
    @Transactional
    public void unbanMember(int serverId, int bannedServerMemberId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        BannedServerMember bannedMember = bannedServerMemberRepo.findByServerIdAndId(serverId, bannedServerMemberId);
        if (server != null && bannedMember != null) {
            bannedServerMemberRepo.delete(bannedMember);
        }
    }
    
    public List<Map<String, Object>> getBannedMembersList(int serverId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            List<Map<String, Object>> list = new ArrayList<>();
            List<BannedServerMember> bannedMembers = bannedServerMemberRepo.findAllByServerId(serverId);
            for (BannedServerMember member : bannedMembers) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("user", member.getUser());
                map.put("banReason", member.getBanReason());
                map.put("banDuration", convertBanDuration(member.getBanDuration()));
                list.add(map);
            }
            return list;
        }
        return null;
    }
    
    private String convertBanDuration(int banDuration) {
        if (banDuration == 0) {
            return "Permanent";
        } else {
            return banDuration + " days";
        }
    }
}
