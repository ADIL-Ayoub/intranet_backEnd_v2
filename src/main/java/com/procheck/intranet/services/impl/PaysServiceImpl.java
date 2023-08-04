package com.procheck.intranet.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKPays;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.payload.request.AffectationPays;
import com.procheck.intranet.repository.PaysReporsitory;
import com.procheck.intranet.services.IPaysService;
import com.procheck.intranet.services.IPersonnelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaysServiceImpl implements IPaysService {
	
	@Autowired
	PaysReporsitory paysReporsitory;

	
	@Autowired
	IPersonnelService personnelService;

	@Override
	public PKPays findPaysById(UUID id) {
		log.info("[SERVICE PAYS] [FIND PAYS BY ID]");
		return paysReporsitory.findById(id).get();
	}


	@Override
	public void affecationEmployees(AffectationPays modele) {
		
			log.info("[ PAYS SERVICE ] ~ [ AFFECTATION PAYS BY LIST ID SERVICES]");
			
			PKPays pays=paysReporsitory.findById(modele.idPays).get();
			
			for (UUID id : modele.idServies) {
				
				List<PKPersonnel> personnels=personnelService.findPersonnelsByService(id);
				
				for (PKPersonnel personnel : personnels) {
					
					personnel.setPkPays(pays);
					
					personnelService.save(personnel);
				}
				
			}
			
		}


	@Override
	public List<PKPays> findAll() {
		log.info("[ PAYS SERVICE ] ~ [ FIND ALL PAYS ]");
		
		return paysReporsitory.findAll();
	}

	
}
