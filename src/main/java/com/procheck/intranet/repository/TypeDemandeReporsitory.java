package com.procheck.intranet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKTypeDemande;

@Repository
public interface TypeDemandeReporsitory  extends JpaRepository<PKTypeDemande, UUID>{

	Boolean existsByCodeTypeDemande(String code);
	
	PKTypeDemande findByCodeTypeDemande(String code);
	
	
}
