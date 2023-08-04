package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKHistoriqueTimesheet;

public interface IHistoriqueTimesheetService {

	void save(PKHistoriqueTimesheet historique);
	
	List<PKHistoriqueTimesheet> findAll();
	
	PKHistoriqueTimesheet findHistoriqueById(UUID id);
	
	PKHistoriqueTimesheet update(PKHistoriqueTimesheet historique);
	
	void delete(PKHistoriqueTimesheet historique);
}
