package com.procheck.intranet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKJoursFerieAnnee;

@Repository
public interface JoursFeierAnneeRepository  extends JpaRepository<PKJoursFerieAnnee, UUID>{

	
}
