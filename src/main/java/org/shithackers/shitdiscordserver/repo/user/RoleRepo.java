package org.shithackers.shitdiscordserver.repo.user;

import org.shithackers.shitdiscordserver.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(Role.ERole name);
}

