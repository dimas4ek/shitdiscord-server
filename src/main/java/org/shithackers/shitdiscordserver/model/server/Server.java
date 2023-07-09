package org.shithackers.shitdiscordserver.model.server;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shithackers.shitdiscordserver.model.user.User;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "servers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;
    
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerChannel> channels;
    
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerChannelCategory> categories;
    
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerMember> members;
    
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerRole> roles;

    @Column(name = "created_at")
    private Date createdAt;
}
