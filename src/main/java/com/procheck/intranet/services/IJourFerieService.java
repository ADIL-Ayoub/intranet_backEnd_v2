package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKJoureFerie;
import com.procheck.intranet.models.PKPays;


public interface IJourFerieService {

	void save(PKJoureFerie jourFerie);

	List<PKJoureFerie> findAll();

	PKJoureFerie findJourFerieById(UUID id);

	PKJoureFerie update(PKJoureFerie jourFerie);

	void delete(PKJoureFerie jourFerie);
	
	List<PKJoureFerie> findJoursFeriesByPaysId(UUID idPays);
}
