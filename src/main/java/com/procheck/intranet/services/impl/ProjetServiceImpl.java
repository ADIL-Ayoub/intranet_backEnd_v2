package com.procheck.intranet.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.ETypeGenerationTs;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKProjet;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.Projet;
import com.procheck.intranet.repository.ProjetReporsitory;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.IProjetService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProjetServiceImpl implements IProjetService {

	@Autowired
	ProjetReporsitory projetReporsitory;

	@Autowired
	IPersonnelService personnelService;

	@Override
	public void save(Projet projetDto) {

		log.info("[ PROJET SERVICE ] ~ [ SAVE PROJET]");
		PKProjet projet=new PKProjet();
		projet.setCodeProjet(projetDto.getCodeProjet());
		projet.setShortNameProjet(projetDto.getShortNameProjet());
		projet.setPerimetre(projetDto.getPerimeter().toLowerCase());

		projetReporsitory.save(projet);
	}

	@Override
	public List<PKProjet> findAll() {

		log.info("[ PROJET SERVICE ] ~ [ FIND ALL PROJET]");
		return projetReporsitory.findAll();
	}

	@Override
	public PKProjet findProjetById(UUID id) {

		log.info("[ PROJET SERVICE ] ~ [ FIND PROJET BY ID]");
		return projetReporsitory.findById(id).get();

	}

	@Override
	public PKProjet update(PKProjet projet) {

		log.info("[ PROJET SERVICE ] ~ [ UPDATE PROJET]");
		return projetReporsitory.save(projet);
	}

	@Override
	public void delete(PKProjet projet) {

		log.info("[ PROJET SERVICE ] ~ [ DELETE PROJET]");
		projetReporsitory.delete(projet);
	}

	@Override
	public void updateGenerationTS(UUID idProjet, GenerationTs generationTs) {

		log.info("[ PROJET SERVICE ] ~ [ UPDATE GTS PROJET BY ID]");
		
		List<PKPersonnel> personnels=personnelService.findPersonnelsByProjet(idProjet);

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

			personnelService.update(personnel);

		}
	}

	@Override
	public List<PKProjet> findProjetsByService(UUID id) {
		log.info("[ PROJET SERVICE ] ~ [ FIND PROJETS BY ID SERVICE ]");
		return projetReporsitory.findByServices_id(id);
	}


	@Override
	public List<PKProjet> findByServiceAndCodeProjet(UUID id, String code) {
		log.info("[ PROJET SERVICE ] ~ [ FIND PROJETS BY ID SERVICE AND CODE ]");
		
		return projetReporsitory.findByServices_idAndCodeProjetLike(id,"%"+code+"%");
		
	}

}
