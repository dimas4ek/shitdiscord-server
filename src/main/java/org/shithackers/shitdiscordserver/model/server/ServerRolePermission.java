package org.shithackers.shitdiscordserver.model.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "server_permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServerRolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Enumerated(EnumType.STRING)
    private PermissionType name;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private List<ServerRole> roles;
    
    public enum PermissionType {
        ADMIN,
        MODERATOR,
        KICK_MEMBERS,
        BAN_MEMBERS,
        VIEW_CHANNELS,
        MANAGE_CHANNELS,
        MANAGE_MESSAGES,
        MANAGE_ROLES,
        SEND_MESSAGES
    }
}
