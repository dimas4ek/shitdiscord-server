package org.shithackers.shitdiscordserver.repo.server;

import org.shithackers.shitdiscordserver.model.server.ServerRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRolePermissionRepo extends JpaRepository<ServerRolePermission, Integer> {
    List<ServerRolePermission> findAllByRolesId(int roleId);
    Optional<ServerRolePermission> findByName(ServerRolePermission.PermissionType name);
}
