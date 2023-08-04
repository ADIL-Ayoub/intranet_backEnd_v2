package com.procheck.intranet.services.impl;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKSemaineTravail;

import com.procheck.intranet.payload.request.AffectationSemaine;
import com.procheck.intranet.repository.SemaineReporsitory;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.ISemaineTravailService;
import com.procheck.intranet.services.IServiceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SemaineServiceImpl implements ISemaineTravailService{

	
	@Autowired
	SemaineReporsitory semaineReporsitory;
	
	@Autowired
	IServiceService serviceService;
	
	@Autowired
	IPersonnelService personnelService;
	
	@Override
	public PKSemaineTravail findByIdPersonnel(UUID personnels) {
		log.info("[ SEMAINE SERVICE ] ~ [ FIND SEMAINE BY LIST ID PERSONNELS]");
		return semaineReporsitory.findByPersonnels_id(personnels);
	}


	@Override
	public PKSemaineTravail findByPersonnels_idAndHoraireByDate(UUID id,String date) {
		log.info("[ SEMAINE SERVICE ] ~ [ FIND SEMAINE BY DATE]");
		return semaineReporsitory.findByPersonnels_idAndHoraires_Jour(id,date);
	}


	@Override
	public void affecationEmployees(AffectationSemaine modele) {
		log.info("[ SEMAINE SERVICE ] ~ [ AFFECTATION SEMAINE BY LIST ID SERVICES]");
		
		PKSemaineTravail semaine=semaineReporsitory.findById(modele.idSemaine).get();
		
		for (UUID id : modele.idServies) {
			
			List<PKPersonnel> personnels=personnelService.findPersonnelsByService(id);
			
			for (PKPersonnel personnel : personnels) {
				
				personnel.setSemaineTravail(semaine);
				
				personnelService.save(personnel);
			}
			
		}
		
	}

	
}
