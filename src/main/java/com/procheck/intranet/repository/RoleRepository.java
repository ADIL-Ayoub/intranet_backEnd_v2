package com.procheck.intranet.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKRole;



@Repository
public interface RoleRepository extends JpaRepository<PKRole, UUID> {
	
	PKRole findByName(String name);
	
	Boolean existsByName(String name);
	
	
	
	
}

