package org.shithackers.shitdiscordserver.service.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.model.server.ServerRole;
import org.shithackers.shitdiscordserver.model.server.ServerRolePermission;
import org.shithackers.shitdiscordserver.payload.request.ServerRolePermissionRequest;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRolePermissionRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServerRoleService {
    private final ServerRoleRepo serverRoleRepo;
    private final ServerRepo serverRepo;
    private final ServerMemberRepo serverMemberRepo;
    private final ServerRolePermissionRepo serverRolePermissionRepo;
    
    @Autowired
    public ServerRoleService(ServerRoleRepo serverRoleRepo, ServerRepo serverRepo, ServerMemberRepo serverMemberRepo, ServerRolePermissionRepo serverRolePermissionRepo) {
        this.serverRoleRepo = serverRoleRepo;
        this.serverRepo = serverRepo;
        this.serverMemberRepo = serverMemberRepo;
        this.serverRolePermissionRepo = serverRolePermissionRepo;
    }
    
    public List<Map<String, Object>> getServerRoles(int serverId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            return serverRoleRepo.findAllByServerId(serverId).stream()
                .map(role -> getRoles(serverId, role))
                .collect(Collectors.toList());
        }
        return null;
    }
    
    private Map<String, Object> getRoles(int serverId, ServerRole role) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", role.getId());
        map.put("name", role.getName());
        map.put("color", role.getColor());
        map.put("permissions", getRolePermissions(serverId, role.getId()));
        return map;
    }
    
    @Transactional
    public ServerRole createRole(int serverId, ServerRole serverRole) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            serverRole.setServer(server);
            serverRoleRepo.save(serverRole);
            return serverRole;
        }
        return null;
    }
    
    public ServerRole showRole(int serverId, int roleId) {
        return serverRoleRepo.findByServerIdAndId(serverId, roleId);
    }
    
    public Map<String, Object> showMembersByRole(int serverId, int roleId) {
        ServerRole role = serverRoleRepo.findByServerIdAndId(serverId, roleId);
        return Map.of(
            "roleId", role.getId(),
            "members", role.getMembers()
        );
    }
    
    @Transactional
    public void addRoleToMember(int serverId, int serverMemberId, int roleId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            ServerMember member = serverMemberRepo.findById(serverMemberId).orElse(null);
            ServerRole role = serverRoleRepo.findByServerIdAndId(serverId, roleId);
            
            if (member != null && role != null) {
                if (!member.getRoles().contains(role)) {
                    member.getRoles().add(role);
                    serverMemberRepo.save(member);
                }
            }
        }
    }
    
    @Transactional
    public void removeRoleFromMember(int serverId, int serverMemberId, int roleId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        if (server != null) {
            ServerMember member = serverMemberRepo.findById(serverMemberId).orElse(null);
            ServerRole role = serverRoleRepo.findByServerIdAndId(serverId, roleId);
            
            if (member != null && role != null) {
                if (member.getRoles().contains(role)) {
                    member.getRoles().remove(role);
                    serverMemberRepo.save(member);
                }
            }
        }
    }
    
    public List<Map<String, Object>> getAllRolesPermissions(int serverId) {
        return serverRepo.findById(serverId)
            .map(server -> server.getRoles().stream()
                .map(role -> showRole(serverId, role))
                .collect(Collectors.toList()))
            .orElse(null);
    }
    
    public List<String> getRolePermissions(int serverId, int roleId) {
        return serverRepo.findById(serverId)
            .map(server -> serverRolePermissionRepo.findAllByRolesId(roleId))
            .orElse(Collections.emptyList())
            .stream()
            .map(permission -> String.valueOf(permission.getName()))
            .collect(Collectors.toList());
    }
    
    public void addPermissionsToRole(int serverId, int roleId, ServerRolePermissionRequest serverRolePermissionRequest) {
        Server server = serverRepo.findById(serverId).orElseThrow(() -> new RuntimeException("Error: Server not found"));
        if(server != null) {
            ServerRole role = serverRoleRepo.findByServerIdAndId(serverId, roleId);
            if(role != null) {
                Set<String> strPermissions = serverRolePermissionRequest.getPermissions();
                Set<ServerRolePermission> permissions = new HashSet<>();
                
                if (strPermissions != null) {
                    strPermissions.forEach(permission -> {
                        ServerRolePermission rolePermission = findRolePermissionByName(permission);
                        
                        if (!role.getPermissions().contains(rolePermission)) {
                            permissions.add(rolePermission);
                        }
                    });
                }
                
                //на клиенте будут учитываться включенные права
                //они пойдут в тело запроса
                //permissions.addAll(role.getPermissions());
                role.setPermissions(permissions);
                serverRoleRepo.save(role);
            }
        }
    }
    
    private ServerRolePermission findRolePermissionByName(String permissionName) {
        ServerRolePermission.PermissionType permissionType = ServerRolePermission.PermissionType.valueOf(permissionName.toUpperCase());
        return serverRolePermissionRepo.findByName(permissionType)
            .orElseThrow(() -> new RuntimeException("Error: Permission not found"));
    }
    
    private Map<String, Object> showRole(int serverId, ServerRole role) {
        Map<String, Object> roleMap = new LinkedHashMap<>();
        roleMap.put("id", role.getId());
        roleMap.put("name", role.getName());
        roleMap.put("permissions", getRolePermissions(serverId, role.getId()));
        return roleMap;
    }
    
    public List<Map<String, Object>> getMemberRoles(int serverId, int serverMemberId) {
        Server server = serverRepo.findById(serverId).orElse(null);
        ServerMember member = serverMemberRepo.findByServerIdAndId(serverId, serverMemberId);
        List<Map<String, Object>> list = new ArrayList<>();
        if (server != null && member != null) {
            for (ServerRole role : member.getRoles()) {
                list.add(getRoles(serverId, role));
            }
            return list;
        }
        return null;
    }
}
