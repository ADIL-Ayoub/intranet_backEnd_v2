package com.procheck.intranet.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKConge;

@Repository
public interface CongeReporsitory extends JpaRepository<PKConge, UUID>{
	
	boolean existsByDateDebutAndDateRepriseAndDemande_Personnel_id(LocalDate dateDebut,LocalDate dateReprise,UUID idPersonnel);
	
	List<PKConge> findByStatusAndDateReprise(String status,LocalDate date);
	
	List<PKConge> findByStatusAndDateDebut(String status,LocalDate date);
}
