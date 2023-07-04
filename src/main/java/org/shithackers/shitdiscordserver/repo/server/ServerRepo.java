package org.shithackers.shitdiscordserver.repo.server;

import org.shithackers.shitdiscordserver.model.server.Server;
import org.shithackers.shitdiscordserver.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerRepo extends JpaRepository<Server, Integer> {
    List<Server> findAllByCreatorId(int creatorId);
    List<Server> findAllByMembersPerson(User person);
}
