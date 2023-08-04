package com.procheck.intranet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKSemaineTravail;

@Repository
public interface SemaineReporsitory extends JpaRepository<PKSemaineTravail, UUID>{

	PKSemaineTravail findByPersonnels_id(UUID personnels);
	
	PKSemaineTravail findByPersonnels_idAndHoraires_Jour(UUID id,String jour);
	
	boolean existsById(UUID id);
	
}
