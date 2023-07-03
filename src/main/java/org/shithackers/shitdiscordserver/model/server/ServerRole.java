package org.shithackers.shitdiscordserver.model.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "server_roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServerRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String name;
    
    private String color;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", referencedColumnName = "id")
    private Server server;
    
    @ManyToMany(mappedBy = "roles")
    private List<ServerMember> members;
}
