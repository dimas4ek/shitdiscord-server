package org.shithackers.shitdiscordserver.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerBanRequest {
    private String banReason;
    private int banDuration;
}
