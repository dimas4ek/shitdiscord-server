package org.shithackers.shitdiscordserver.model.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "server_channels")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServerChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", referencedColumnName = "id")
    private Server server;

    @JsonIgnore
    @OneToMany(mappedBy = "serverChannel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerChannelMessage> serverChannelMessages;

    private String name;

    private String type;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private ServerChannelCategory category;

    @Column(name = "created_at")
    private Date createdAt;
}
