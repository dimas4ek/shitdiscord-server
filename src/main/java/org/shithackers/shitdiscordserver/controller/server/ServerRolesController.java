package org.shithackers.shitdiscordserver.controller.server;

import org.shithackers.shitdiscordserver.model.server.ServerRole;
import org.shithackers.shitdiscordserver.service.server.ServerMemberService;
import org.shithackers.shitdiscordserver.service.server.ServerRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/servers/{serverId}")
public class ServerRolesController {
    private final ServerRoleService serverRoleService;
    private final ServerMemberService serverMemberService;
    
    public ServerRolesController(ServerRoleService serverRoleService, ServerMemberService serverMemberService) {
        this.serverRoleService = serverRoleService;
        this.serverMemberService = serverMemberService;
    }
    
    @GetMapping("/roles")
    public List<Map<String, Object>> showRoles(@PathVariable int serverId) {
        return serverRoleService.getServerRoles(serverId);
    }
    
    @PostMapping("/roles")
    public ResponseEntity<?> createNewRole(@PathVariable int serverId, @RequestBody ServerRole serverRole) {
        return new ResponseEntity<>(serverRoleService.createRole(serverId, serverRole), HttpStatus.CREATED);
    }
    
    @GetMapping("/roles/{roleId}")
    public ServerRole showRole(@PathVariable int roleId, @PathVariable int serverId) {
        return serverRoleService.showRole(serverId, roleId);
    }
    
    @GetMapping("/roles/{roleId}/members")
    public Map<String, Object> showMembersByRole(@PathVariable int roleId, @PathVariable int serverId) {
        return serverRoleService.showMembersByRole(serverId, roleId);
    }
    
    @GetMapping("/{serverMemberId}/roles")
    public Map<String, Object> showMemberRoles(@PathVariable int serverId, @PathVariable int serverMemberId) {
        return serverMemberService.getServerMemberInfo(serverId, serverMemberId);
    }
    
    @PutMapping("/{serverMemberId}/roles/{roleId}")
    public ResponseEntity<?> addRoleToMember(@PathVariable int serverId,
                                             @PathVariable int serverMemberId,
                                             @PathVariable int roleId) {
        serverRoleService.addRoleToMember(serverId, serverMemberId, roleId);
        
        return new ResponseEntity<>(
            serverMemberService.getServerMemberInfo(serverId, serverMemberId),
            HttpStatus.CREATED
        );
    }
    
    @DeleteMapping("/{serverMemberId}/roles/{roleId}")
    public ResponseEntity<?> removeRoleFromMember(@PathVariable int serverId,
                                                  @PathVariable int serverMemberId,
                                                  @PathVariable int roleId) {
        serverRoleService.removeRoleFromMember(serverId, serverMemberId, roleId);
        
        return ResponseEntity.ok(
            serverMemberService.getServerMemberInfo(serverId, serverMemberId)
        );
    }
}
