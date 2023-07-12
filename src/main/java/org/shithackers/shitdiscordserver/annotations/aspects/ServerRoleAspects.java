package org.shithackers.shitdiscordserver.annotations.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.shithackers.shitdiscordserver.annotations.RolePermissionRequired;
import org.shithackers.shitdiscordserver.model.server.ServerRole;
import org.shithackers.shitdiscordserver.model.server.ServerRolePermission;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRolePermissionRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerRoleRepo;
import org.shithackers.shitdiscordserver.utils.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServerRoleAspects {
    private final static Logger logger = LoggerFactory.getLogger(ServerRoleAspects.class);
    private final ServerRoleRepo serverRoleRepo;
    private final ServerRolePermissionRepo serverRolePermissionRepo;
    private final ServerMemberRepo serverMemberRepo;
    
    public ServerRoleAspects(ServerRoleRepo serverRoleRepo, ServerRolePermissionRepo serverRolePermissionRepo, ServerMemberRepo serverMemberRepo) {
        this.serverRoleRepo = serverRoleRepo;
        this.serverRolePermissionRepo = serverRolePermissionRepo;
        this.serverMemberRepo = serverMemberRepo;
    }
    
    @Around("@annotation(permissionRequired)")
    public Object checkAdminAccess(ProceedingJoinPoint joinPoint, RolePermissionRequired permissionRequired) throws Throwable {
        Object[] args = joinPoint.getArgs();
        int serverId = 0;
        for (Object arg : args) {
            if (arg instanceof Integer) {
                serverId = (int) arg;
                break;
            }
        }
        
        ServerRolePermission rolePermission = serverRolePermissionRepo.findByName(permissionRequired.value()).orElse(null);
        for (ServerRole role : serverRoleRepo.findAllByServerIdAndMembersId(serverId, serverMemberRepo.findByPerson(AuthUtils.getPerson()).getId())) {
            if (role.getPermissions().contains(rolePermission)) {
                return joinPoint.proceed();
            }
        }
        
        logger.error("Access denied (" +
                         joinPoint.getSignature().getDeclaringType().getSimpleName() + "::" +
                         joinPoint.getSignature().getName() +
                         "). " + permissionRequired.value().name() + " role is required.");
        return ResponseEntity.badRequest().body("Access denied. " + permissionRequired.value().name() + " role is required");
    }
    
}
