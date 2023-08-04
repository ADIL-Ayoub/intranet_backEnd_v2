package com.procheck.intranet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKProjetTimesheet;

@Repository
public interface ProjetTimesheetRepository extends JpaRepository<PKProjetTimesheet, UUID>{
	
	Boolean existsByProjetAndTimesheet_id(String projet,UUID  idTimesheet);

	
}
