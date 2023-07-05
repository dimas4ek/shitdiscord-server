package org.shithackers.shitdiscordserver.controller;

import org.shithackers.shitdiscordserver.model.server.ServerMember;
import org.shithackers.shitdiscordserver.service.server.ServerMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servers/{serverId}")
public class ServerMemberController {
    private final ServerMemberService serverMemberService;
    
    @Autowired
    public ServerMemberController(ServerMemberService serverMemberService) {
        this.serverMemberService = serverMemberService;
    }
    
    @GetMapping("/members")
    public List<ServerMember> getServerMembers(@PathVariable int serverId) {
        return serverMemberService.getServerMembers(serverId);
    }
    
    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getServerMember(@PathVariable int serverId, @PathVariable int memberId) {
        ServerMember member = serverMemberService.getServerMember(serverId, memberId);
        if (member != null) {
            return ResponseEntity.ok(member);
        }
        return ResponseEntity.badRequest()
            .body("Server or member not found");
    }
}
