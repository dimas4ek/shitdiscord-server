package org.shithackers.shitdiscordserver.controller;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerChannel;
import org.shithackers.shitdiscordserver.model.server.ServerChannelCategory;
import org.shithackers.shitdiscordserver.service.server.ServerChannelCategoryService;
import org.shithackers.shitdiscordserver.service.server.ServerChannelService;
import org.shithackers.shitdiscordserver.service.server.ServerService;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ServerController {
    private final ServerService serverService;
    private final ServerChannelService serverChannelService;
    private final ServerChannelCategoryService serverChannelCategoryService;

    @Autowired
    public ServerController(ServerService serverService, ServerChannelService serverChannelService, ServerChannelCategoryService serverChannelCategoryService) {
        this.serverService = serverService;
        this.serverChannelService = serverChannelService;
        this.serverChannelCategoryService = serverChannelCategoryService;
    }
    
    
    @GetMapping("/servers")
    public List<Map<String, Object>> serverList() {
        if(AuthUtils.checkLoggedIn()) {
            return serverService.getServerList();
        }
        return null;
    }

    @GetMapping("/servers/{serverId}")
    public Map<String, Object> showServer(@PathVariable int serverId) {
        Map<String, Object> map = new HashMap<>();
        map.put("server", serverService.getServerInfo(serverId));

        return map;
    }
    
    @PostMapping("/joinServer")
    public ResponseEntity<?> joinServer(@RequestParam("serverId") int serverId) {
        serverService.joinServer(serverId);
        
        Server server = serverService.getServer(serverId);
        if(server != null) {
            return ResponseEntity.ok(server);
        }
        return ResponseEntity.badRequest()
            .body("Server not found");
    }

    @PostMapping("/createServer")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Server> createServer(@RequestBody Server server) {
        return new ResponseEntity<>(serverService.saveRest(server), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{serverId}/kickFromServer")
    public ResponseEntity<?> kickFromServer(@PathVariable int serverId,
                                            @RequestParam("memberId") int memberId) {
        Server server = serverService.getServer(serverId);
        if(server != null) {
            if (serverService.kickFromServer(serverId, memberId)) {
                return ResponseEntity.ok(serverService.getServerMemberList(server));
            }
            return ResponseEntity.badRequest()
                .body("Server member not found");
        }
        return ResponseEntity.badRequest()
            .body("Server not found");
    }
    
    @DeleteMapping("/servers/{serverId}")
    public ResponseEntity<String> deleteServer(@PathVariable int serverId) {
        Server server = serverService.getServer(serverId);
        serverService.deleteServer(server);
        return ResponseEntity.ok("Server has been deleted");
    }
    
    @GetMapping("/servers/{serverId}/getServerChannels")
    public ResponseEntity<?> getServerChannels(@PathVariable int serverId) {
        return ResponseEntity.ok(serverChannelService.getServerChannels(serverId));
    }

    @PostMapping("/servers/{serverId}/createServerChannel")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createServerChannel(@RequestBody ServerChannel serverChannel, @PathVariable int serverId) {
        return new ResponseEntity<>(serverChannelService.save(serverId, serverChannel), HttpStatus.CREATED);
    }
    
    @GetMapping("/servers/{serverId}/getServerCategories")
    public ResponseEntity<?> getServerCategories(@PathVariable int serverId) {
        return ResponseEntity.ok(serverChannelCategoryService.getServerCategories(serverId));
    }
    
    @GetMapping("/servers/{serverId}/categories/{serverCategoryId}")
    public ResponseEntity<?> getServerCategory(@PathVariable int serverId, @PathVariable int serverCategoryId) {
        return ResponseEntity.ok(serverChannelCategoryService.getServerCategory(serverId, serverCategoryId));
    }
    
    @PostMapping("/servers/{serverId}/createServerChannelCategory")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ServerChannelCategory> createServerChannelCategory(@RequestBody ServerChannelCategory serverChannelCategory, @PathVariable int serverId) {
        return new ResponseEntity<>(serverChannelCategoryService.save(serverId, serverChannelCategory), HttpStatus.CREATED);
    }
    
    @PutMapping("/servers/{serverId}/{serverCategoryId}/{serverChannelId}")
    public ResponseEntity<?> addChannelToCategory(@PathVariable int serverId, @PathVariable int serverCategoryId, @PathVariable int serverChannelId) {
        serverChannelCategoryService.addChannelToCategory(serverId, serverCategoryId, serverChannelId);
        
        return ResponseEntity.ok(serverChannelCategoryService.getServerCategory(serverId, serverCategoryId));
    }
    
    @DeleteMapping("/servers/{serverId}/deleteServerChannel/{serverChannelId}")
    public ResponseEntity<String> deleteServerChannel(@PathVariable int serverChannelId, @PathVariable int serverId) {
        Server server = serverService.getServer(serverId);
        ServerChannel serverChannel = serverChannelService.getServerChannel(server, serverChannelId);
        serverChannelService.deleteServerChannel(serverChannel);
        return ResponseEntity.ok("Server channel '" + serverChannel.getName() + " has been deleted");
    }
    
    @DeleteMapping("/servers/{serverId}/deleteServerCategory/{serverCategoryId}")
    public ResponseEntity<?> deleteServerCategory(@PathVariable int serverCategoryId, @PathVariable int serverId) {
        serverChannelCategoryService.deleteServerCategory(serverCategoryId, serverId);
        return ResponseEntity.ok(serverChannelCategoryService.getServerCategories(serverId));
    }
}
