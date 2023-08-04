package com.procheck.intranet.services.impl;


import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.ETypeGenerationTs;
import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.repository.DepartementReporsitory;

import com.procheck.intranet.services.IDepartementService;
import com.procheck.intranet.services.IPersonnelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepartementServiceImpl implements IDepartementService {

	@Autowired
	DepartementReporsitory departementReporsitory;

	@Autowired
	IPersonnelService personnelService;

	@Override
	public void save(PKDepartement departement) {
		log.info("[ DEPARTEMENT SERVICE ] ~ [ SAVE DEPARTEMENT]");
		departementReporsitory.save(departement);

	}

	@Override
	public Page<PKDepartement> findAll(Pageable pageable) {
		log.info("[ DEPARTEMENT SERVICE ] ~ [ FIND ALL DEPARTEMENTS]");
		return departementReporsitory.findAll(pageable);
	}

	@Override
	public PKDepartement findDepartemenetById(UUID id) {
		log.info("[ DEPARTEMENT SERVICE ] ~ [ FIND DEPARTEMENT BY ID]");
		return departementReporsitory.findById(id).get();
	}

	@Override
	public PKDepartement update(PKDepartement departement) {
		log.info("[ DEPARTEMENT SERVICE ] ~ [ UPDATE  DEPARTEMENT]");
		return departementReporsitory.save(departement);
	}

	@Override
	public void delete(PKDepartement departement) {
		log.info("[ DEPARTEMENT SERVICE ] ~ [ DELETE DEPARTEMENT]");
		departementReporsitory.delete(departement);

	}

	@Override
	public PKDepartement findDepartementByCode(String code) {
		log.info("[ DEPARTEMENT SERVICE ] ~ [ FIND DEPARTEMENT BY CODE]");
		return departementReporsitory.findByCodeDepartement(code).get();
	}

	@Override
	public void updateGenerationTS(UUID idDepartement,GenerationTs generationTs) {

		log.info("[ DEPARTEMENT SERVICE ] ~ [ UPDATE GTS DEPARTEMENT BY ID]");

		List<PKPersonnel> personnels = personnelService.findPersonnelsByDepartement(idDepartement);

		for (PKPersonnel personnel : personnels) {

			personnel.setBGenerationTs(generationTs.fGenerationTs);
			String type = generationTs.getSTypeGenerationTs().toLowerCase();
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
	public Collection<PKClient> findClientsByDept(UUID id) {
		log.info("[ DEPARTEMENT SERVICE ] ~ [ FIND CLIENTS BY DPET]");
		PKDepartement dpet=findDepartemenetById(id);
		return dpet.getClients();
	}


}
