package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerChannel;
import org.shithackers.shitdiscordserver.model.server.ServerChannelCategory;
import org.shithackers.shitdiscordserver.model.server.ServerChannelMessage;
import org.shithackers.shitdiscordserver.model.user.User;
import org.shithackers.shitdiscordserver.repo.server.ServerChannelCategoryRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerChannelMessageRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerChannelRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.shithackers.shitdiscordserver.websocket.model.WSServerChannelMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ServerChannelService {
    private final ServerChannelRepo serverChannelRepo;
    private final ServerChannelCategoryRepo serverChannelCategoryRepo;
    private final ServerChannelMessageRepo serverChannelMessageRepo;
    private final ServerRepo serverRepo;
    
    @Autowired
    public ServerChannelService(ServerChannelRepo serverChannelRepo, ServerChannelCategoryRepo serverChannelCategoryRepo, ServerChannelMessageRepo serverChannelMessageRepo, ServerRepo serverRepo) {
        this.serverChannelRepo = serverChannelRepo;
        this.serverChannelCategoryRepo = serverChannelCategoryRepo;
        this.serverChannelMessageRepo = serverChannelMessageRepo;
        this.serverRepo = serverRepo;
    }
    
    public ServerChannel getServerChannel(Server server, int channelId) {
        List<ServerChannel> serverChannels = serverChannelRepo.findAllByServer(server);
        if (serverChannels.isEmpty()) {
            return null;
        }
        return serverChannelRepo.findByServerAndId(server, channelId);
    }
    
    public void sendMessage(int serverId, int serverChannelId, User sender, String message) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerChannel serverChannel = serverChannelRepo.findByServerAndId(server, serverChannelId);
        
        ServerChannelMessage serverChannelMessage = new ServerChannelMessage();
        serverChannelMessage.setServer(server);
        serverChannelMessage.setServerChannel(serverChannel);
        serverChannelMessage.setSender(sender);
        serverChannelMessage.setMessage(message);
        serverChannelMessage.setCreatedAt(new Date());
        
        serverChannelMessageRepo.save(serverChannelMessage);
    }
    
    public ServerChannelMessage sendMessageRest(int serverId, int serverChannelId, User sender, String message) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerChannel serverChannel = serverChannelRepo.findByServerAndId(server, serverChannelId);
        
        ServerChannelMessage serverChannelMessage = new ServerChannelMessage();
        serverChannelMessage.setServer(server);
        serverChannelMessage.setServerChannel(serverChannel);
        serverChannelMessage.setSender(sender);
        serverChannelMessage.setMessage(message);
        serverChannelMessage.setCreatedAt(new Date());
        
        serverChannelMessageRepo.save(serverChannelMessage);
        
        return serverChannelMessage;
    }
    
    public String getServerChannelNameById(int serverChannelId) {
        ServerChannel serverChannel = serverChannelRepo.findById(serverChannelId).orElse(null);
        if (serverChannel != null) {
            return serverChannel.getName();
        }
        return null;
    }
    
    public String getServerChannelId(Server server) {
        List<ServerChannel> serverChannels = serverChannelRepo.findAllByServer(server);
        if (serverChannels.isEmpty()) {
            return null;
        }
        return String.valueOf(serverChannels.get(0).getId());
    }
    
    public ServerChannel createFirstServerChannel(Server server) {
        ServerChannel serverChannel = new ServerChannel();
        
        ServerChannelCategory serverChannelCategory = new ServerChannelCategory();
        serverChannelCategory.setServer(server);
        serverChannelCategory.setName("text channels");
        serverChannelCategoryRepo.save(serverChannelCategory);
        
        serverChannel.setServer(server);
        serverChannel.setName("general");
        serverChannel.setType("text");
        serverChannel.setCategory(serverChannelCategory);
        serverChannel.setCreatedAt(new Date());
        
        serverChannelRepo.save(serverChannel);
        
        return serverChannel;
    }
    
    public List<ServerChannel> getServerChannels(Server server) {
        return serverChannelRepo.findAllByServer(server);
    }
    
    public void save(ServerChannel serverChannel) {
        serverChannel.setCreatedAt(new Date());
        serverChannelRepo.save(serverChannel);
    }
    
    public ServerChannel saveRest(ServerChannel serverChannel) {
        serverChannel.setCreatedAt(new Date());
        serverChannelRepo.save(serverChannel);
        
        return serverChannel;
    }
    
    public String redirectToServerChannel(ServerChannel serverChannel) {
        return "redirect:/channels/" + serverChannel.getServer().getId() + "/" + serverChannel.getId();
    }
    
    public void deleteServerChannel(ServerChannel serverChannel) {
        //когда будет готова обработка сообщений
        //удалить сообщения из канала
        serverChannelRepo.delete(serverChannel);
    }
    
    public List<WSServerChannelMessage> getServerChannelMessages(int serverId, int serverChannelId) {
        List<WSServerChannelMessage> wsServerChannelMessages = new ArrayList<>();
        for (ServerChannelMessage serverChannelMessage : serverChannelMessageRepo.findAllByServerIdAndServerChannelId(serverId, serverChannelId)) {
            WSServerChannelMessage wsServerChannelMessage = new WSServerChannelMessage(
                serverChannelMessage.getServer().getId(),
                serverChannelMessage.getId(),
                serverChannelMessage.getSender().getUsername(),
                serverChannelMessage.getMessage(),
                serverChannelMessage.getCreatedAt()
            );
            wsServerChannelMessages.add(wsServerChannelMessage);
        }
        return wsServerChannelMessages;
    }
    
    public void deleteServerChannelMessage(int serverId, int serverChannelId, int messageId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerChannel serverChannel = serverChannelRepo.findByServerAndId(server, serverChannelId);
        ServerChannelMessage message = serverChannelMessageRepo.findById(messageId).orElse(null);
        if (server != null && serverChannel != null && message != null) {
            serverChannel.getServerChannelMessages().remove(message);
        }
    }
}
