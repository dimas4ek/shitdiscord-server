package org.shithackers.shitdiscordserver.controller;

import org.shithackers.shitdiscordserver.payload.request.ServerRolePermissionRequest;
import org.shithackers.shitdiscordserver.service.server.ServerRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/servers/{serverId}")
public class ServerPermissionsController {
    private final ServerRoleService serverRoleService;
    
    public ServerPermissionsController(ServerRoleService serverRoleService) {
        this.serverRoleService = serverRoleService;
    }
    
    @GetMapping("/roles/permissions")
    public List<Map<String, Object>> getAllPermissions(@PathVariable int serverId) {
        return serverRoleService.getAllRolesPermissions(serverId);
    }
    
    @GetMapping("/roles/{roleId}/permissions")
    public List<String> getRolePermissions(@PathVariable int serverId, @PathVariable int roleId) {
        return serverRoleService.getRolePermissions(serverId, roleId);
    }
    
    @PostMapping("/roles/{roleId}/permissions")
    public ResponseEntity<?> addPermissionsToRole(
        @PathVariable int serverId,
        @PathVariable int roleId,
        @RequestBody ServerRolePermissionRequest serverRolePermissionRequest
    ) {
        serverRoleService.addPermissionsToRole(serverId, roleId, serverRolePermissionRequest);
        
        return new ResponseEntity<>(
            serverRoleService.getRolePermissions(serverId, roleId),
            HttpStatus.CREATED
        );
    }
}
