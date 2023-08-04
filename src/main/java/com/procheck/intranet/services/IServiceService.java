package com.procheck.intranet.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKService;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.ParametrageResponsable;
import com.procheck.intranet.payload.response.DepartementResponse;

public interface IServiceService {

	void save(PKService service);
	
	Page<PKService> findAll(Pageable pageable);
	
	Page<PKService> findServicesByClient(UUID idClient,Pageable pageable);
	
	List<PKService> findServiceByIdClient(UUID idClient);
	
	PKService findServiceById(UUID id);
	
	PKService findServiceByCode(String code);
	
	PKService findServiceByCodeAndService(String code,PKClient client);
	
	PKService update(PKService service);
	
	void delete(PKService service);
	
	void updateGenerationTS(UUID idService,GenerationTs generationTs);
	
	void assignResponsableService(List<UUID> idServices,UUID idPersonnel);
	
	void assignSuperviseurService(List<UUID> idServices,UUID idPersonnel);
	
	
	Set<PKDepartement> findDepartementsByResponsable(UUID idUser);
	Set<PKClient> findClientsByResponsable(UUID idResponsable);
	List<PKService> findServicesByResponsable(UUID idResponsable);
	
	
	Set<PKDepartement> findDepartementsBySuperviseur(UUID idSuperviseur);
	Set<PKClient> findClientsBySuperviseur(UUID idSuperviseur);
	List<PKService> findServicesBySuperviseur(UUID idSuperviseur);
	
	void deleteResponsable(UUID id);
	
	void deleteSuperviseur(UUID id);

	void activeResponsable(ParametrageResponsable parametre);
	
	
	void assignProjetsToService(UUID id,List<UUID> projets);
	

	
}
