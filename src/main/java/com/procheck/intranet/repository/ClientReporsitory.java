package com.procheck.intranet.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;



public interface ClientReporsitory extends JpaRepository<PKClient, UUID>{
		
	Optional<PKClient> findByCodeClient(String code);
	
	Optional<PKClient> findByCodeClientAndDepartement(String code,PKDepartement dept);

	Boolean existsByCodeClientAndDepartement(String code,PKDepartement departement);

	Boolean existsByShortNameClient(String sShortNameClient);
	
	Boolean existsByShortNameClientAndDepartement(String sShortNameClient,PKDepartement departement);

	Optional<PKClient> findById(UUID id);

	Optional<PKClient> findByShortNameClient(String sShortNameClient);

	Page<PKClient> findByDepartement_id(UUID idDepartement, Pageable pageable);
	
	
}
