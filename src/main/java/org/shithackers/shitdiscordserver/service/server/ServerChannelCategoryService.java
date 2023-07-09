package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerChannel;
import org.shithackers.shitdiscordserver.model.server.ServerChannelCategory;
import org.shithackers.shitdiscordserver.repo.server.ServerChannelCategoryRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerChannelRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerChannelCategoryService {
    private final ServerChannelCategoryRepo serverChannelCategoryRepo;
    private final ServerRepo serverRepo;
    private final ServerChannelRepo serverChannelRepo;
    
    @Autowired
    public ServerChannelCategoryService(ServerChannelCategoryRepo serverChannelCategoryRepo, ServerRepo serverRepo, ServerChannelRepo serverChannelRepo) {
        this.serverChannelCategoryRepo = serverChannelCategoryRepo;
        this.serverRepo = serverRepo;
        this.serverChannelRepo = serverChannelRepo;
    }
    
    public ServerChannelCategory save(int serverId, ServerChannelCategory serverChannelCategory) {
        serverChannelCategory.setServer(serverRepo.findById(serverId).orElse(null));
        return serverChannelCategoryRepo.save(serverChannelCategory);
    }
    
    public void deleteServerCategory(int serverCategoryId, int serverId) {
        serverRepo.findById(serverId).ifPresent(server -> serverChannelCategoryRepo.deleteById(serverCategoryId));
    }
    
    /*public Map<String, Object> getServerCategories(int serverId) {
        List<ServerChannelCategory> categories = serverChannelCategoryRepo.findAllByServerId(serverId);
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, Object> result = new LinkedHashMap<>();
        for (ServerChannelCategory category : categories) {
            map.put("id", category.getId());
            map.put("channels", category.getServerChannels().stream().map(serverChannel -> {
                Map<String, Object> channelMap = new LinkedHashMap<>();
                channelMap.put("name", serverChannel.getName());
                channelMap.put("type", serverChannel.getType());
                return channelMap;
            }));
            result.put(category.getName(), map);
        }
        return result;
    }*/
    
    public List<ServerChannelCategory> getServerCategories(int serverId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            return serverChannelCategoryRepo.findAllByServerId(serverId);
        }
        return null;
    }
    
    public void addChannelToCategory(int serverId, int serverCategoryId, int serverChannelId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            ServerChannel channel = serverChannelRepo.findByServerAndId(server, serverChannelId);
            ServerChannelCategory category = serverChannelCategoryRepo.findById(serverCategoryId).orElse(null);
            if (channel != null && category != null) {
                channel.setCategory(category);
                serverChannelRepo.save(channel);
            }
        }
    }
    
    public ServerChannelCategory getServerCategory(int serverId, int serverCategoryId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            return serverChannelCategoryRepo.findById(serverCategoryId).orElse(null);
        }
        return null;
    }
}
