package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKHistoriqueTransfaire;

public interface IHistoriqueTransfaireService {

	void save(PKHistoriqueTransfaire historiqueTransfaireDto);
	
	List<PKHistoriqueTransfaire> findAll();
	
	PKHistoriqueTransfaire findHistoriqueTransfaireById(UUID id);
	
	PKHistoriqueTransfaire update(PKHistoriqueTransfaire historiqueTransfaire);
	
	void delete(PKHistoriqueTransfaire historiqueTransfaire);
}
