package org.shithackers.shitdiscordserver.model.server;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "server_channel_category")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServerChannelCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", referencedColumnName = "id")
    private Server server;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ServerChannel> serverChannel;
    
    private String name;
}
