package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.ServerChannelCategory;
import org.shithackers.shitdiscordserver.repo.server.ServerChannelCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerChannelCategoryService {
    private final ServerChannelCategoryRepo serverChannelCategoryRepo;
    
    @Autowired
    public ServerChannelCategoryService(ServerChannelCategoryRepo serverChannelCategoryRepo) {
        this.serverChannelCategoryRepo = serverChannelCategoryRepo;
    }
    
    public ServerChannelCategory save(ServerChannelCategory serverChannelCategory) {
        return serverChannelCategoryRepo.save(serverChannelCategory);
    }
    
    public ServerChannelCategory findById(int id) {
        return serverChannelCategoryRepo.findById(id).orElse(null);
    }
    
    public void delete(int id) {
        serverChannelCategoryRepo.deleteById(id);
    }
}
