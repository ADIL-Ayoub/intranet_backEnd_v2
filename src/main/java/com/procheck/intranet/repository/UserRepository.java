package com.procheck.intranet.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKUser;

@Repository
public interface UserRepository extends JpaRepository<PKUser, UUID>{

	Optional<PKUser> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Optional<PKUser> findById(UUID id);

	PKUser findByEmail(String email);
}
