package org.shithackers.shitdiscordserver.model.friend;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.shithackers.shitdiscordserver.model.user.User;

import java.util.Date;

@Entity
@Table(name = "friend_requests")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private User receiver;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private Date createdAt;

    public enum FriendRequestStatus {
        PENDING,
        ACCEPTED,
        DECLINED
    }
}
