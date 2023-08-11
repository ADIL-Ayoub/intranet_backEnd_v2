package com.procheck.intranet.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKDemande;

import com.procheck.intranet.payload.request.DemandeFilter;
import com.procheck.intranet.repository.DemandeReporsitory;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.IDemandeService;
import com.procheck.intranet.services.specifications.DemandeSpec;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandeServiceImpl implements IDemandeService{
	
	@Autowired
	DemandeReporsitory demandeReporsitory;
	
	@Autowired
	IUserDetailsService userService;
	
	@Autowired
	DemandeSpec demandeSpec;
	
	
	@Override
	public PKDemande save(PKDemande demande) {
		log.info("[ DEMANDE SERVICE ] ~ [ CREATE DEMANDE ]");
		return demandeReporsitory.save(demande);
	}

	@Override
	public List<PKDemande> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PKDemande findDemandeById(UUID id) {
		log.info("[ DEMANDE SERVICE ] ~ [ FIND DEMANDE BY ID]");
		return demandeReporsitory.findById(id).get();
	}

	@Override
	public PKDemande update(PKDemande demande) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(PKDemande demande) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PKDemande> findByPersonnelAndTypedemande(UUID idPersonnel, UUID idTypeDmd) {
		log.info("[ DEMANDE SERVICE ] ~ [ FIND DEMANDE BY PERSONNE AND TYPE DEMANDE]");
		return demandeReporsitory.findByPersonnel_idAndTypedemande_id(idPersonnel, idTypeDmd);
	}

	@Override
	public List<PKDemande> findByRespAndTypedemande(UUID idSuper, UUID idTypeDmd) {
		log.info("[ DEMANDE SERVICE ] ~ [ FIND DEMANDE BY SUPERVISEUR AND TYPE DEMANDE]");
		return demandeReporsitory.findByCodeSupAndTypedemande_id(idSuper, idTypeDmd);
	}

	@Override
	public List<PKDemande> findByCodeDemandeurAndTypedemandeAndStatus(String userName, UUID idTypeDmd, String status) {
		log.info("[ DEMANDE SERVICE ] ~ [ FIND DEMANDE BY SUPERVISEUR AND TYPE DEMANDE AND STATUS ]");
		return demandeReporsitory.findByCodeDemandeurAndTypedemande_idAndConges_status(userName, idTypeDmd, status);
	}

	@Override
	public List<PKDemande> getDemandeByFilter(DemandeFilter filter) {
		log.info("[ DEMANDE SERVICE ] ~ [ FIND DEMANDE BY FILTER ]");
		return demandeSpec.getDemandsByFilter(filter);
	}

	@Override
	public List<PKDemande> findByCodeSup(UUID codeSup) {
		log.info("[ DEMANDE SERVICE ] ~ [ FIND DEMANDES BY CODE SUP ]");
		return demandeReporsitory.findAllByCodeSup(codeSup);

	}

}
