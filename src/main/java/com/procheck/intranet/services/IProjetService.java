package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKProjet;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.Projet;

public interface IProjetService {

	void save(Projet projetDto);

	List<PKProjet> findAll();

	PKProjet findProjetById(UUID id);

	PKProjet update(PKProjet projet);

	void delete(PKProjet projet);

	void updateGenerationTS(UUID idProjet, GenerationTs generationTs);
	
	List<PKProjet> findProjetsByService(UUID id);
	
	List<PKProjet> findByServiceAndCodeProjet(UUID id,String code);
			
}
