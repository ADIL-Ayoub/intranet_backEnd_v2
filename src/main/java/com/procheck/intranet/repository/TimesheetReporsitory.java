package com.procheck.intranet.repository;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import com.procheck.intranet.models.PKTimesheet;

@Repository
public interface TimesheetReporsitory extends JpaRepository<PKTimesheet, UUID> ,JpaSpecificationExecutor<PKTimesheet>{


	PKTimesheet findByDateTimesheetAndPersonnel_id(LocalDate date,UUID personne);

	boolean existsByDateTimesheetAndPersonnel_id(LocalDate date,UUID personne);
	
}
