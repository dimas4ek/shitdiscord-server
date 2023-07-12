package org.shithackers.shitdiscordserver.annotations;

import org.shithackers.shitdiscordserver.model.server.ServerRolePermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RolePermissionRequired {
    ServerRolePermission.PermissionType value();
}
