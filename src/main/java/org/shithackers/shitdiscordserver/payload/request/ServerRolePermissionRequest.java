package org.shithackers.shitdiscordserver.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ServerRolePermissionRequest {
    private Set<String> permissions;
}
