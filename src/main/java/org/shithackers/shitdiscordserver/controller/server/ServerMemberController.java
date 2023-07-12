package org.shithackers.shitdiscordserver.controller.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.payload.request.ServerBanRequest;
import org.shithackers.shitdiscordserver.repo.server.BannedServerMemberRepo;
import org.shithackers.shitdiscordserver.repo.server.ServerMemberRepo;
import org.shithackers.shitdiscordserver.service.server.ServerMemberService;
import org.shithackers.shitdiscordserver.service.server.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/servers/{serverId}")
public class ServerMemberController {
    private final ServerMemberService serverMemberService;
    private final ServerService serverService;
    private final ServerMemberRepo serverMemberRepo;
    private final BannedServerMemberRepo bannedServerMemberRepo;
    
    @Autowired
    public ServerMemberController(ServerMemberService serverMemberService, ServerService serverService, ServerMemberRepo serverMemberRepo, BannedServerMemberRepo bannedServerMemberRepo) {
        this.serverMemberService = serverMemberService;
        this.serverService = serverService;
        this.serverMemberRepo = serverMemberRepo;
        this.bannedServerMemberRepo = bannedServerMemberRepo;
    }
    
    @GetMapping("/members")
    public List<Map<String, Object>> getServerMembers(@PathVariable int serverId) {
        return serverMemberService.getServerMembers(serverId);
    }
    
    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getServerMember(@PathVariable int serverId, @PathVariable int memberId) {
        return ResponseEntity.ok(serverMemberService.getServerMemberInfo(serverId, memberId));
    }
    
    @PostMapping("/members/{memberId}/changeUsername")
    public ResponseEntity<?> changeUsername(@PathVariable int serverId, @PathVariable int memberId, @RequestBody String newUsername) {
        serverMemberService.changeUsername(serverId, memberId, newUsername);
        
        return ResponseEntity.ok(serverMemberService.getServerMemberInfo(serverId, memberId));
    }
    
    @DeleteMapping("/members/{serverMemberId}/kick")
    public ResponseEntity<?> kickMember(@PathVariable int serverId,
                                        @PathVariable int serverMemberId) {
        Server server = serverService.getServer(serverId);
        String memberUsername = serverMemberRepo.findByServerIdAndId(serverId, serverMemberId).getServerUsername();
        if (server != null) {
            serverService.kickMember(serverId, serverMemberId);
            return ResponseEntity.ok(memberUsername + " has been kicked from server " + server.getName());
        }
        return ResponseEntity.badRequest()
            .body("Bad request");
    }
    
    @DeleteMapping("/members/{serverMemberId}/ban")
    public ResponseEntity<?> banMember(@PathVariable int serverId,
                                       @PathVariable int serverMemberId,
                                       @RequestBody ServerBanRequest banRequest) {
        Server server = serverService.getServer(serverId);
        String memberUsername = serverMemberRepo.findByServerIdAndId(serverId, serverMemberId).getServerUsername();
        if (server != null) {
            serverService.banMember(serverId, serverMemberId, banRequest);
            return ResponseEntity.ok(memberUsername + " has been banned from server " + server.getName());
        }
        return ResponseEntity.badRequest()
            .body("Bad request");
    }
    
    @DeleteMapping("/members/{serverMemberId}/unban")
    public ResponseEntity<?> unbanMember(@PathVariable int serverId,
                                         @PathVariable int serverMemberId) {
        Server server = serverService.getServer(serverId);
        String memberUsername = bannedServerMemberRepo.findByServerIdAndId(serverId, serverMemberId).getUser().getUsername();
        if (server != null) {
            serverService.unbanMember(serverId, serverMemberId);
            return ResponseEntity.ok(memberUsername + " has been unbanned for server " + server.getName());
        }
        return ResponseEntity.badRequest()
            .body("Bad request");
    }
}
