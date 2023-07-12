package org.shithackers.shitdiscordserver.controller.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.service.server.ServerService;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ServerStuffController {
    private final ServerService serverService;
    
    @Autowired
    public ServerStuffController(ServerService serverService) {
        this.serverService = serverService;
    }
    
    @GetMapping("/servers")
    public List<Map<String, Object>> serverList() {
        if (AuthUtils.checkLoggedIn()) {
            return serverService.getServerList();
        }
        return null;
    }
    
    @PostMapping("/createServer")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Server> createServer(@RequestBody Server server) {
        return new ResponseEntity<>(serverService.createServer(server), HttpStatus.CREATED);
    }
}
