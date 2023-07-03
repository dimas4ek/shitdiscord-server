package org.shithackers.shitdiscordserver.controller;

import org.shithackers.shitdiscordserver.model.server.ServerRole;
import org.shithackers.shitdiscordserver.service.server.ServerRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ServerRolesController {
    private final ServerRoleService serverRoleService;
    
    public ServerRolesController(ServerRoleService serverRoleService) {
        this.serverRoleService = serverRoleService;
    }
    
    @GetMapping("/channels/{serverId}/roles")
    public List<ServerRole> showRoles(@PathVariable int serverId) {
        return serverRoleService.getServerRoles(serverId);
    }
    
    @GetMapping("/channels/{serverId}/roles/{roleId}")
    public ServerRole showRole(@PathVariable int roleId, @PathVariable int serverId) {
        return serverRoleService.showRole(serverId, roleId);
    }
    
    @PostMapping("/channels/{serverId}/createNewRole")
    public ResponseEntity<?> createNewRole(@PathVariable int serverId, @RequestBody ServerRole serverRole) {
        return new ResponseEntity<>(serverRoleService.createRole(serverId, serverRole), HttpStatus.CREATED);
    }
    
    @GetMapping("/channels/{serverId}/{serverMemberId}/roles")
    public Map<String, Object> showMemberRoles(@PathVariable int serverId, @PathVariable int serverMemberId) {
        return serverRoleService.showMemberRoles(serverId, serverMemberId);
    }
    
    @PostMapping("/channels/{serverId}/{serverMemberId}/addRole")
    public ResponseEntity<?> addRoleToMember(@PathVariable int serverId,
                                             @PathVariable int serverMemberId,
                                             @RequestParam("roleId") int roleId) {
        serverRoleService.addRoleToMember(serverId, serverMemberId, roleId);
        
        return new ResponseEntity<>(
            serverRoleService.showMemberRoles(serverId, serverMemberId),
            HttpStatus.CREATED
        );
    }
    
    @DeleteMapping("/channels/{serverId}/{serverMemberId}/removeRole")
    public ResponseEntity<?> removeRoleFromMember(@PathVariable int serverId,
                                                  @PathVariable int serverMemberId,
                                                  @RequestParam("roleId") int roleId) {
        serverRoleService.removeRoleFromMember(serverId, serverMemberId, roleId);
        
        return ResponseEntity.ok(
            serverRoleService.showMemberRoles(serverId, serverMemberId)
        );
    }
}
