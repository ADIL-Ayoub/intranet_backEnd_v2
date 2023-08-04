package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKEtatDemande;

public interface IEtatDemandeService {

	void save(PKEtatDemande etatDemande);
	
	List<PKEtatDemande> findAll();
	
	PKEtatDemande findDemandeById(UUID id);
	
	PKEtatDemande update(PKEtatDemande etatDemande);
	
	void delete(PKEtatDemande etatDemande);
}
