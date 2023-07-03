package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerChannel;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ServerService {
    private final ServerRepo serverRepo;
    private final ServerChannelService serverChannelService;
    private final ServerMemberRepo serverMemberRepo;
    
    @Autowired
    public ServerService(ServerRepo serverRepo, ServerChannelService serverChannelService, ServerMemberRepo serverMemberRepo) {
        this.serverRepo = serverRepo;
        this.serverChannelService = serverChannelService;
        this.serverMemberRepo = serverMemberRepo;
    }
    
    public Server getServer(int serverId) {
        return serverRepo.findById(serverId).orElse(null);
    }
    
    @Transactional
    public void save(Server server) {
        server.setCreator(AuthUtils.getPerson());
        server.setCreatedAt(new Date());
        serverRepo.save(server);
        
        ServerMember serverMember = new ServerMember();
        serverMember.setServer(server);
        serverMember.setPerson(AuthUtils.getPerson());
        serverMember.setJoinDate(new Date());
        serverMemberRepo.save(serverMember);
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
    
    public String redirectToServer(Server server) {
        ServerChannel serverChannel = serverChannelService.createFirstServerChannel(server);
        
        return "redirect:/channels/" + server.getId() + "/" + serverChannel.getId();
    }
    
    public List<Server> getServerList() {
        return new ArrayList<>(serverRepo.findAllByMembersPerson(AuthUtils.getPerson()));
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
