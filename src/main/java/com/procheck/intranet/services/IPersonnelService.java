package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKSemaineTravail;
import com.procheck.intranet.models.PKService;
import com.procheck.intranet.models.PKTimesheet;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.PersonnelFilter;
import com.procheck.intranet.payload.request.PersonnelFilterByService;


public interface IPersonnelService {

	void save(PKPersonnel personnel);
	
	Page<PKPersonnel> findAll(Pageable pageable);
	
	PKPersonnel findPersonnelById(UUID id);
	
	PKPersonnel findPersonnelByCin(String cin);
	
	PKPersonnel update(PKPersonnel personnel);
	
	PKPersonnel assignUser(UUID idPersonnel,PKUser user);
	
	PKPersonnel deleteUser(UUID idPersonnel,PKUser user);
	
	PKPersonnel findPersonnelByUser(PKUser user);
	
	void delete(PKPersonnel personnel);
	
	List<PKPersonnel> findPersonnelsByService(PKService service);
	
	List<PKPersonnel> findPersonnelsByIdService(UUID id);
	
	void updateGenerationTS(UUID idPersonnel,GenerationTs generationTs);

	List<PKPersonnel> findPersonnelsByService(UUID service);
	
	List<PKPersonnel> findPersonnelsByClient(UUID client);
	
	List<PKPersonnel> findPersonnelsByDepartement(UUID departement);
	
	List<PKPersonnel> findPersonnelsByProjet(UUID Projet);
	
	List<PKPersonnel> findPersonnelsByTimesheets(List<PKTimesheet> timesheets);
	
	List<PKPersonnel> findPersonnelBySemaineTravail(PKSemaineTravail semaineTravail);
	
	Page<PKPersonnel> findPersonnelByCinOrNameOrPrenomeOrPoste(PersonnelFilter personnel,int size,int page );
	
	Page<PKPersonnel> findPersonnelByFilterAll(PersonnelFilter personnel,int size,int page );
	
	List<PKPersonnel> findPersonnelByServicesAndFilter(PersonnelFilterByService personnel);
	
	void updateEmployee(UUID idPersonne,UUID idSuper);
	
	
	
}
