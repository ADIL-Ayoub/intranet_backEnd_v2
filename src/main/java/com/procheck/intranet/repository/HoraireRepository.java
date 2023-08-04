package com.procheck.intranet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKHoraire;

@Repository
public interface HoraireRepository extends JpaRepository<PKHoraire, UUID>{

	PKHoraire  findByJourIgnoreCaseAndSemaineTravails_Id(String sJour,UUID idSemaine);
	
	boolean existsByJourIgnoreCaseAndSemaineTravails_Id(String sJour,UUID idSemaine);
	
	
	
	
}
