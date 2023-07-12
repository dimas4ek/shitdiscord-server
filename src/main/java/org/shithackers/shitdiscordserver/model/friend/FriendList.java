package org.shithackers.shitdiscordserver.model.friend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shithackers.shitdiscordserver.model.user.User;

import java.util.Date;

@Entity
@Table(name = "friend_list")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FriendList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private User person;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", referencedColumnName = "id")
    private User friend;
    
    @Column(name = "created_at")
    private Date createdAt;
}
