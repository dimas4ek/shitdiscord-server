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
    private Server activeServer;

    @Autowired
    public ServerController(ServerService serverService, ServerChannelService serverChannelService, ServerChannelCategoryService serverChannelCategoryService) {
        this.serverService = serverService;
        this.serverChannelService = serverChannelService;
        this.serverChannelCategoryService = serverChannelCategoryService;
    }
    
    
    @GetMapping("/servers")
    public List<Server> serverList() {
        if(AuthUtils.checkLoggedIn()) {
            return serverService.getServerList();
        }
        return null;
    }

    @GetMapping("/channels/{serverId}")
    public Map<String, Object> showServer(@PathVariable int serverId) {
        Server server = serverService.getServer(serverId);

        activeServer = server;

        Map<String, Object> map = new HashMap<>();
        map.put("server", server);
        
        //проверить почему у канала category: null
        //в рест ответе

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
    
    @DeleteMapping("/channels/{serverId}")
    public ResponseEntity<String> deleteServer(@PathVariable int serverId) {
        Server server = serverService.getServer(serverId);
        serverService.deleteServer(server);
        return ResponseEntity.ok("Server '" + activeServer.getName() + "' has been deleted");
    }

    @PostMapping("/createServerChannel")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ServerChannel> createServerChannel(@RequestBody ServerChannel serverChannel) {
        serverChannel.setServer(activeServer);
        return new ResponseEntity<>(serverChannelService.saveRest(serverChannel), HttpStatus.CREATED);
    }
    
    @PostMapping("/createServerChannelCategory")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ServerChannelCategory> createServerChannelCategory(@RequestBody ServerChannelCategory serverChannelCategory) {
        serverChannelCategory.setServer(activeServer);
        return new ResponseEntity<>(serverChannelCategoryService.save(serverChannelCategory), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/channels/{serverId}/{serverChannelId}")
    public ResponseEntity<String> deleteServerChannel(@PathVariable int serverChannelId, @PathVariable int serverId) {
        Server server = serverService.getServer(serverId);
        ServerChannel serverChannel = serverChannelService.getServerChannel(server, serverChannelId);
        serverChannelService.deleteServerChannel(serverChannel);
        return ResponseEntity.ok("Server channel '" + serverChannel.getName() + "' from server '" + activeServer.getName() + "' has been deleted");
    }
}
