package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.procheck.intranet.models.PKDemande;
import com.procheck.intranet.payload.request.DemandeFilter;

public interface IDemandeService {

	PKDemande save(PKDemande demande);
	
	List<PKDemande> findAll();
	
	PKDemande findDemandeById(UUID id);
	
	PKDemande update(PKDemande demande);
	
	void delete(PKDemande demande);
	
	List<PKDemande> findByCodeDemandeurAndTypedemandeAndStatus(String userName,UUID idTypeDmd,String status);
	
	List<PKDemande> findByPersonnelAndTypedemande(UUID idPersonne,UUID idTypeDmd);
	
	List<PKDemande> findByRespAndTypedemande(UUID idSuper,UUID idTypeDmd);
	
	List<PKDemande> getDemandeByFilter(DemandeFilter filter);
	
}
