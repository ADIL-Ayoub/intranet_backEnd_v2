package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKEtatTimesheet;

public interface IEtatTimeSheetService {

	void save(PKEtatTimesheet etatTimesheet);
	
	List<PKEtatTimesheet> findAll();
	
	PKEtatTimesheet findEtatTimesheetById(UUID id);
	
	PKEtatTimesheet update(PKEtatTimesheet etatTimesheett);
	
	void delete(PKEtatTimesheet etatTimesheet);
}
