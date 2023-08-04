package com.procheck.intranet.services;


import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.payload.request.GenerationTs;

public interface IClientService {

	void save(PKClient client);
	
	Page<PKClient> findAll(Pageable pageable);
	
	Page<PKClient> findClientsByDepartement(UUID departement,Pageable pageable);
	
	PKClient findClientById(UUID id);
	
	PKClient findClientByCode(String code);
	
	PKClient finClientByCodeAndDept(String code,PKDepartement dept);
	
	PKClient update(PKClient client);
	
	void delete(PKClient client);
	
	void updateGenerationTS(UUID idClient,GenerationTs generationTs);
	
}
