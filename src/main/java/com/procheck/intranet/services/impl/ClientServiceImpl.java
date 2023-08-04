package com.procheck.intranet.services.impl;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKPersonnel;

import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.models.ETypeGenerationTs;
import com.procheck.intranet.repository.ClientReporsitory;
import com.procheck.intranet.services.IClientService;
import com.procheck.intranet.services.IPersonnelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClientServiceImpl implements IClientService{

	@Autowired
	ClientReporsitory clientreporsitory;
	
	@Autowired
	IPersonnelService personnelService;
	
	@Override
	public void save(PKClient client) {
		log.info("[ CLIENT SERVICE ] ~ [ SAVE CLIENT]");
		clientreporsitory.save(client);
		
	}

	@Override
	public Page<PKClient> findAll(Pageable pageable) {
		log.info("[ CLIENT SERVICE ] ~ [ FIND ALL CLIENT]");
		return clientreporsitory.findAll(pageable);
	}

	@Override
	public PKClient findClientById(UUID id) {
		log.info("[ CLIENT SERVICE ] ~ [ FIND CLIENT BY ID]");
		return clientreporsitory.findById(id).get();
	}

	@Override
	public PKClient update(PKClient client) {
		log.info("[ CLIENT SERVICE ] ~ [ UPDATE CLIENT]");
		return clientreporsitory.save(client);
	}

	@Override
	public void delete(PKClient client) {
		log.info("[ CLIENT SERVICE ] ~ [ DELETE CLIENT]");
		clientreporsitory.delete(client);
		
	}

	@Override
	public PKClient findClientByCode(String code) {
		log.info("[ CLIENT SERVICE ] ~ [ FIND CLIENT BY CODE]");
		return clientreporsitory.findByCodeClient(code).get();
	}

	@Override
	public PKClient finClientByCodeAndDept(String code, PKDepartement dept) {
		log.info("[ CLIENT SERVICE ] ~ [ FIND CLIENT BY CODE AND DEPT]");
		return clientreporsitory.findByCodeClientAndDepartement(code, dept).get();
	}

	@Override
	public void updateGenerationTS(UUID idClient,GenerationTs generationTs) {
		
		log.info("[ CLIENT SERVICE ] ~ [ UPDATE GTS CLIENT BY ID]");

		List<PKPersonnel> personnels=personnelService.findPersonnelsByClient(idClient);
		
		
		
		System.out.println(generationTs.toString());
		
		for (PKPersonnel personnel : personnels) {
			
			personnel.setBGenerationTs(generationTs.fGenerationTs);
			String type=generationTs.getSTypeGenerationTs().toLowerCase();
			switch (type) {
			case "pointeuse":
				personnel.setSTypeGenerationTs(ETypeGenerationTs.Pointeuse.name());
				break;
			case "horaire":
				personnel.setSTypeGenerationTs(ETypeGenerationTs.horaire.name());
				break;
			case "none":
				personnel.setSTypeGenerationTs(ETypeGenerationTs.Manuel.name());
				break;
			default:
				break;
			}
			
			personnel.setBProjetTs(generationTs.isFProjetTs());
			personnel.setType(generationTs.getType());
			personnelService.update(personnel);
			
			
		}

	}

	@Override
	public Page<PKClient> findClientsByDepartement(UUID idDepartement, Pageable pageable) {
		log.info("[ CLIENT SERVICE ] ~ [ FIND CLIENTS BY  DEPT]");
		return clientreporsitory.findByDepartement_id(idDepartement,pageable);
		
	}

	
}

