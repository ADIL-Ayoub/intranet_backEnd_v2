package com.procheck.intranet.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.procheck.intranet.payload.request.MesConges;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.ETypeGenerationTs;
import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKProjet;
import com.procheck.intranet.models.PKSemaineTravail;
import com.procheck.intranet.models.PKService;
import com.procheck.intranet.models.PKTimesheet;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.PersonnelFilter;
import com.procheck.intranet.payload.request.PersonnelFilterByService;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.services.IClientService;
import com.procheck.intranet.services.IDepartementService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.IProjetService;
import com.procheck.intranet.services.IServiceService;
import com.procheck.intranet.services.specifications.PersonnelFilterAll;
import com.procheck.intranet.services.specifications.PersonnelSpec;
import com.procheck.intranet.services.specifications.PersonnelSpecByService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersonnelServiceImpl implements IPersonnelService {

	@Autowired
	PersonnelReporsitory personnelReporsitory;

	@Autowired
	IClientService clientService;

	@Autowired
	IDepartementService departementService;

	@Autowired
	IProjetService projetService;

	@Autowired
	IServiceService serviceService;

	@Autowired
	PersonnelSpec personnelSpec;
	
	@Autowired
	PersonnelFilterAll personnelSpecAll;

	@Autowired
	PersonnelSpecByService personnelSpecByService;

	@Override
	public void save(PKPersonnel personnel) {
		log.info("[ PERSONNEL SERVICE ] ~ [ SAVE PERSONNEL]");
		personnelReporsitory.save(personnel);

	}

	@Override
	public Page<PKPersonnel> findAll(Pageable pageable) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND ALL PERSONNELS]");
		return personnelReporsitory.findAll(pageable);
	}

	@Override
	public PKPersonnel findPersonnelById(UUID id) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND ONE PERSONNEL BY ID]");
		return personnelReporsitory.findById(id).get();
	}

	@Override
	public PKPersonnel findPersonnelByCin(String cin) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND ONE PERSONNEL BY CIN]");
		return personnelReporsitory.findByCin(cin).get();
	}

	@Override
	public PKPersonnel update(PKPersonnel personnel) {
		log.info("[ PERSONNEL SERVICE ] ~ [ UPDATE PERSONNEL]");
		return personnelReporsitory.save(personnel);
	}

	@Override
	public void delete(PKPersonnel personnel) {
		log.info("[ PERSONNEL SERVICE ] ~ [ DELETE PERSONNEL]");
		personnelReporsitory.delete(personnel);

	}

	@Override
	public List<PKPersonnel> findPersonnelsByService(PKService service) {

		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY SERVICE]");
		return personnelReporsitory.findByService(service);
	}

	@Override
	public List<PKPersonnel> findPersonnelsByService(UUID idService) {

		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY ID SERVICE]");

		PKService service = serviceService.findServiceById(idService);

		List<PKPersonnel> personnels = personnelReporsitory.findByService(service);

		log.info("[ PERSONNEL SERVICE ] ~ [ PERSONNELS BY SERVICE COUNT : {}]", personnels.size());

		return personnels;
	}

	@Override
	public List<PKPersonnel> findPersonnelsByClient(UUID idClient) {

		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY CLIENT]");
		List<PKPersonnel> personnels = new ArrayList<>();

		PKClient client = clientService.findClientById(idClient);

		Collection<PKService> services = client.getServices();

		for (PKService service : services) {

			personnels.addAll(service.getPersonnels());

		}
		log.info("[ PERSONNEL SERVICE ] ~ [ PERSONNELS BY CLIENT COUNT : {}]", personnels.size());
		return personnels;
	}

	@Override
	public List<PKPersonnel> findPersonnelsByDepartement(UUID idDepartement) {

		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY DEPARTEMENT]");
		List<PKPersonnel> personnels = new ArrayList<>();

		PKDepartement departement = departementService.findDepartemenetById(idDepartement);

		for (PKClient client : departement.getClients()) {

			for (PKService service : client.getServices()) {

				personnels.addAll(service.getPersonnels());

			}
		}

		log.info("[ PERSONNEL SERVICE ] ~ [ PERSONNELS BY DEPARTEMENT COUNT : {}]", personnels.size());
		return personnels;
	}

	@Override
	public List<PKPersonnel> findPersonnelsByTimesheets(List<PKTimesheet> timesheets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PKPersonnel> findPersonnelBySemaineTravail(PKSemaineTravail semaineTravail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PKPersonnel> findPersonnelsByProjet(UUID idProjet) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY PROJET]");
		List<PKPersonnel> personnels = new ArrayList<PKPersonnel>();

		PKProjet projet = projetService.findProjetById(idProjet);

		for (PKService service : projet.getServices()) {

			personnels.addAll(service.getPersonnels());
		}
		log.info("[ PERSONNEL SERVICE ] ~ [ PERSONNELS BY PROJET COUNT : {}]", personnels.size());
		return personnels;
	}

	@Override
	public Page<PKPersonnel> findPersonnelByCinOrNameOrPrenomeOrPoste(PersonnelFilter personnel, int size, int page) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY CIN OR NAME OR PRENOM]");

		Page<PKPersonnel> personnels = personnelSpec.getPersonnelsByFilter(personnel, size, page);

		return personnels;
	}

	@Override
	public PKPersonnel assignUser(UUID idPersonnel, PKUser user) {
		log.info("[ PERSONNEL SERVICE ] ~ [ ASSIGN USER PERSONNEL]");
		PKPersonnel personnel = findPersonnelById(idPersonnel);

		personnel.setUser(user);
		personnel.setBAffectation(true);

		return personnelReporsitory.save(personnel);
	}

	@Override
	public PKPersonnel findPersonnelByUser(PKUser user) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNEL BY USER]");

		return personnelReporsitory.findByUser(user);
	}

	@Override
	public PKPersonnel deleteUser(UUID idPersonnel, PKUser user) {
		log.info("[ PERSONNEL SERVICE ] ~ [ ASSIGN USER PERSONNEL]");
		PKPersonnel personnel = findPersonnelById(idPersonnel);

		personnel.setUser(null);
		personnel.setBAffectation(false);
		return personnelReporsitory.save(personnel);
	}

	@Override
	public List<PKPersonnel> findPersonnelByServicesAndFilter(PersonnelFilterByService filter) {
		log.info(
				"[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY SERVICES AND CIN OR NAME OR PRENOM OR MATRUCULE OR POSTE ]");

			List<PKPersonnel> personnels = personnelSpecByService.getPersonnelsByFilter(filter);

		return personnels;
	}

	@Override
	public List<PKPersonnel> findPersonnelsByIdService(UUID id) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY ID SERVICE]");
		return personnelReporsitory.findByService_id(id);
	}

	@Override
	public void updateGenerationTS(UUID idPersonnel, GenerationTs generationTs) {
		log.info("[ PERSONNEL SERVICE ] ~ [ UPDATE GTS PERSONNEL BY ID]");

		PKPersonnel personnel = findPersonnelById(idPersonnel);

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
			update(personnel);		
	}

	@Override
	public Page<PKPersonnel> findPersonnelByFilterAll(PersonnelFilter personnel, int size, int page) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY CIN OR NAME OR PRENOM ALL]");

		Page<PKPersonnel> personnels = personnelSpecAll.getPersonnelsByFilter(personnel, size, page);
		//List<PKPersonnel> list= personnelReporsitory.findPersonnelByFilterAll(personnel.cin,personnel.nom,personnel.prenom,personnel.sPoste);
		//Page<PKPersonnel> personnels=new PageImpl<PKPersonnel>(list,PageRequest.of(page,size),list.size());
		//<MesConges> pages = new PageImpl<MesConges>(listHolder.getPageList(),
				//PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), conges.size());
		return personnels;
	}

	@Override
	public void updateEmployee(UUID idPersonne, UUID idSuper) {
		log.info("[ PERSONNEL SERVICE ] ~ [ UPDATE SUPERIEUR HIERARCHIQUE PERSONNEL BY ID]");
		
		PKPersonnel personnel=findPersonnelById(idPersonne);
		personnel.setSuperieur(idSuper);
		personnelReporsitory.save(personnel);
		
	}

	//mon code

	@Override
	public List<PKPersonnel> findPersonnelByCodeSuperieur(UUID codeSuperieur) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY CODE SUPERIEUR]");
		return personnelReporsitory.findBySuperieur(codeSuperieur);
	}

	@Override
	public List<PKPersonnel> findPersonnelByCodeSuperieurAndCINOrNomOrPrenom(UUID codeSuperieur,String search) {
		log.info("[ PERSONNEL SERVICE ] ~ [ FIND PERSONNELS BY CODE SUPERIEUR]");
		return personnelReporsitory.findBySuperieurAndNomOrPrenomOrCin(codeSuperieur,search,search,search);
	}

}
