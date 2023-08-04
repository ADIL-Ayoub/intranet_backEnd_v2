package com.procheck.intranet.repository;

import java.util.UUID;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKPrivilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<PKPrivilege, UUID> {

	PKPrivilege findByName(String name);
	
	Boolean existsByName(String name);

}
