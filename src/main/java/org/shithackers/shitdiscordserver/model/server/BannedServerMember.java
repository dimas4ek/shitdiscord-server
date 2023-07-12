package org.shithackers.shitdiscordserver.model.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shithackers.shitdiscordserver.model.user.User;

import java.util.Date;

@Entity
@Table(name = "banned_server_members")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BannedServerMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private User user;
    
    @Column(name = "ban_reason")
    private String banReason;
    
    private int banDuration;
    
    @Column(name = "banned_at")
    private Date bannedAt;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", referencedColumnName = "id")
    private Server server;
}
