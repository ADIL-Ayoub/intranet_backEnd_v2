package com.procheck.intranet.services;



import java.util.Collection;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.payload.request.GenerationTs;


public interface IDepartementService {

	void save(PKDepartement departemenet);
	
	Page<PKDepartement> findAll(Pageable pageable);
	
	PKDepartement findDepartemenetById(UUID id);
	
	Collection<PKClient> findClientsByDept(UUID id);
	
	PKDepartement findDepartementByCode(String code);
	
	PKDepartement update(PKDepartement departemenet);
	
	void delete(PKDepartement departemenet);
	
	void updateGenerationTS(UUID idDepartement,GenerationTs generationTs);
	
	
}
