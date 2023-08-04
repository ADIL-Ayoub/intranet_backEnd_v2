package com.procheck.intranet.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKConge;
import com.procheck.intranet.payload.request.Conge;

public interface ICongeService {

	void save(PKConge conge);
	
	List<PKConge> findAll();
	
	List<PKConge> findByStatusAndDateReprise(String status,LocalDate date);
	
	List<PKConge> findByStatusAndDateDebut(String status,LocalDate date);
	
	PKConge findCongeById(UUID id);
	
	PKConge update(PKConge conge);
	
	void delete(PKConge conge);

	List<Conge> enregistreConges(UUID idUser, List<Conge> conges);
}
