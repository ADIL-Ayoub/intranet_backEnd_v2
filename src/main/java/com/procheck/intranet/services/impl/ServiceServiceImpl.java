package com.procheck.intranet.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.ETypeGenerationTs;
import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKProjet;
import com.procheck.intranet.models.PKService;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.ParametrageResponsable;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.security.services.IRoleService;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.IClientService;
import com.procheck.intranet.services.IDepartementService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.IProjetService;
import com.procheck.intranet.services.IServiceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ServiceServiceImpl implements IServiceService {

	@Autowired
	ServiceReporsitory servicerepo;

	@Autowired
	IPersonnelService personnelService;

	@Autowired
	IRoleService roleService;

	@Autowired
	IUserDetailsService userService;

	@Autowired
	IClientService clientService;

	@Autowired
	IDepartementService departementService;
	
	@Autowired
	IProjetService projetService;

	@Override
	public void save(PKService service) {
		log.info("[ SERVICE SERVICE ] ~ [ SAVE SERVICE]");
		servicerepo.save(service);

	}

	@Override
	public Page<PKService> findAll(Pageable pageable) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND ALL SERVICES]");
		return servicerepo.findAll(pageable);
	}

	@Override
	public PKService findServiceById(UUID id) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND SERVICE BY ID]");
		return servicerepo.findById(id).get();
	}

	@Override
	public PKService update(PKService service) {
		log.info("[ SERVICE SERVICE ] ~ [ UPDATE SERVICE]");
		return servicerepo.save(service);
	}

	@Override
	public void delete(PKService service) {
		log.info("[ SERVICE SERVICE ] ~ [ DELETE SERVICE]");
		servicerepo.delete(service);

	}

	@Override
	public PKService findServiceByCode(String code) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND SERVICE BY CODE]");
		return servicerepo.findByCodeService(code).get();
	}

	@Override
	public PKService findServiceByCodeAndService(String code, PKClient client) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND SERVICE BY CODE AND CLIENT]");
		return servicerepo.findByCodeServiceAndClient(code, client).get();
	}

	@Override
	public void updateGenerationTS(UUID idService, GenerationTs generationTs) {

		log.info("[ SERVICE SERVICE ] ~ [ UPDATE GTS SERVICE BY ID]");

		List<PKPersonnel> personnels = personnelService.findPersonnelsByService(servicerepo.findById(idService).get());

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
	public void assignResponsableService(List<UUID> idServices, UUID idPersonnel) {

		log.info("[ SERVICE SERVICE ] ~ [ ASSIGN RESPONSABLE SERVICE BY ID]");
//		PKPersonnel personnel=personnelService.findPersonnelById(idPersonnel);
//		personnel.setService(null);
//		personnelService.save(personnel);
		for (UUID idService : idServices) {
			PKService service = findServiceById(idService);
			service.setCodeResponsable(idPersonnel);
			servicerepo.save(service);
		}

	}

	@Override
	public void assignSuperviseurService(List<UUID> idServices, UUID idPersonnel) {

		log.info("[ SERVICE SERVICE ] ~ [ ASSIGN SUPERVISEUR SERVICE BY ID]");
//		PKPersonnel personnel=personnelService.findPersonnelById(idPersonnel);
//		personnel.setService(null);
//		personnelService.save(personnel);
		for (UUID idService : idServices) {

			PKService service = findServiceById(idService);
			service.setCodeSuperviseur(idPersonnel);
			servicerepo.save(service);
		}
	}

	@Override
	public Set<PKDepartement> findDepartementsByResponsable(UUID idUser) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND DEPARTEMENTS BY ID RESPONSABLE ]");
		List<PKService> services = new ArrayList<PKService>();

		PKUser user = userService.findOne(idUser);
		List<String> roles = roleService.getNameRoleByUser(user);

		boolean isResponsable = roles.contains("RESPONSABLE");
		boolean isSuperviseur = roles.contains("SUPERVISEUR");

		if (isResponsable) {
			services = servicerepo.findByCodeResponsable(user.getPersonnel().getId());			
		}
		if (isSuperviseur) {
			services.addAll(servicerepo.findByCodeSuperviseur(user.getPersonnel().getId()));
		}

		Set<PKClient> clients = new HashSet<PKClient>();
		Set<PKDepartement> departements = new HashSet<PKDepartement>();
		for (PKService service : services) {			
			clients.add(service.getClient());
		}
		for (PKClient client : clients) {
			departements.add(client.getDepartement());
		}
		for (PKDepartement departement : departements) {
			for (PKClient client : departement.getClients()) {
				client.getServices().retainAll(services);
			}
		}
		return departements;
	}

	@Override
	public Set<PKClient> findClientsByResponsable(UUID idResponsable) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND DEPARTEMENTS BY ID RESPONSABLE ]");
		List<PKService> services = servicerepo.findByCodeResponsable(idResponsable);
		Set<PKClient> clients = new HashSet<PKClient>();
		for (PKService service : services) {
			clients.add(service.getClient());
		}
		return clients;
	}

	@Override
	public List<PKService> findServicesByResponsable(UUID idResponsable) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND DEPARTEMENTS BY ID RESPONSABLE ]");
		return servicerepo.findByCodeResponsable(idResponsable);
	}

	@Override
	public Set<PKDepartement> findDepartementsBySuperviseur(UUID idSuperviseur) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND SERVICE BY ID SUPERVISEUR ]");
		List<PKService> services = servicerepo.findByCodeResponsable(idSuperviseur);
		Set<PKClient> clients = new HashSet<PKClient>();
		Set<PKDepartement> departements = new HashSet<PKDepartement>();
		for (PKService service : services) {
			clients.add(service.getClient());
		}
		for (PKClient client : clients) {
			departements.add(client.getDepartement());
		}
		for (PKDepartement departement : departements) {
			for (PKClient client : departement.getClients()) {
				client.getServices().retainAll(services);
			}
		}
		return departements;
	}

	@Override
	public Set<PKClient> findClientsBySuperviseur(UUID idSuperviseur) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND CLIENTS BY ID SUPERVISEUR ]");
		List<PKService> services = servicerepo.findByCodeResponsable(idSuperviseur);
		Set<PKClient> clients = new HashSet<PKClient>();
		for (PKService service : services) {
			clients.add(service.getClient());
		}
		return clients;
	}

	@Override
	public List<PKService> findServicesBySuperviseur(UUID idSuperviseur) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND SERVICES BY ID SUPERVISEUR ]");
		return findServicesBySuperviseur(idSuperviseur);
	}

	@Override
	public void deleteResponsable(UUID id) {
		log.info("[ SERVICE SERVICE ] ~ [ DELETE RESPONSABLE WITH SERVICE]");
		PKService service = findServiceById(id);
		service.setCodeResponsable(null);
		servicerepo.save(service);
	}

	@Override
	public void deleteSuperviseur(UUID id) {
		log.info("[ SERVICE SERVICE ] ~ [ DELETE SUPERVISEUR WITH SERVICE]");
		PKService service = findServiceById(id);
		service.setCodeSuperviseur(null);
		servicerepo.save(service);
	}

	@Override
	public Page<PKService> findServicesByClient(UUID idClient, Pageable pageable) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND SERVICES WITH ID CLIENTS PAGE ]");
		return servicerepo.findByClient_id(idClient, pageable);
	}

	@Override
	public List<PKService> findServiceByIdClient(UUID idClient) {
		log.info("[ SERVICE SERVICE ] ~ [ FIND SERVICES WITH ID CLIENTS ]");
		return servicerepo.findByClient_id(idClient);
	}

	@Override
	public void activeResponsable(ParametrageResponsable parametre) {
		log.info("[ SERVICE SERVICE ] ~ [ ACTIVE VALIDATION RESPONSABLE WITH SERVICE]");
		List<PKService> services = new ArrayList<PKService>();
		if (parametre.getOrganisme().equals("service")) {

			PKService service = findServiceById(parametre.getId());
			service.setActiveRespo(parametre.isActive());
			servicerepo.save(service);

		} else if (parametre.getOrganisme().equals("client")) {

			PKClient client = clientService.findClientById(parametre.getId());

			for (PKService service : client.getServices()) {
				service.setActiveRespo(parametre.isActive());
				services.add(service);
			}
			servicerepo.saveAll(services);
		} else if (parametre.getOrganisme().equals("departement")) {

			PKDepartement departement = departementService.findDepartemenetById(parametre.getId());
			List<PKClient> clients = new ArrayList<PKClient>();
			for (PKClient client : departement.getClients()) {

				clients.add(client);
			}
			for (PKClient client : clients) {
				for (PKService service : client.getServices()) {

					service.setActiveRespo(parametre.isActive());
					services.add(service);
				}
				servicerepo.saveAll(services);
			}
		}
	}

	@Override
	public void assignProjetsToService(UUID id, List<UUID> projets) {
		log.info("[ SERVICE SERVICE ] ~ [ AFFECTATION DES PROJETS TO SERVICE]");
		PKService service=findServiceById(id);
		List<PKProjet> projeList=new ArrayList<PKProjet>();
		for (UUID uuid : projets) {
			
			PKProjet projet=projetService.findProjetById(uuid);
			projeList.add(projet);
		}
		
		service.setProjets(projeList);
		save(service);
	}
}
