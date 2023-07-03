package org.shithackers.shitdiscordserver.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WSMessage {
    private int channelId;
    private String sender;
    private String content;
    private Date createdAt;
}
