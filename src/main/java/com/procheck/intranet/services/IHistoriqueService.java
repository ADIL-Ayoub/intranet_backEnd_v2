package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKHistorique;

public interface IHistoriqueService {

	void save(PKHistorique historique);
	
	List<PKHistorique> findAll();
	
	PKHistorique findHistoriqueById(UUID id);
	
	PKHistorique update(PKHistorique historique);
	
	void delete(PKHistorique historique);
}
