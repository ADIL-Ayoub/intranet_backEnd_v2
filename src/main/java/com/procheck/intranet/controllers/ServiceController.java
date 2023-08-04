package com.procheck.intranet.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKService;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.ParametrageResponsable;
import com.procheck.intranet.payload.response.Client;
import com.procheck.intranet.payload.response.DepartementResponse;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.payload.response.ServiceResponse;
import com.procheck.intranet.repository.ClientReporsitory;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.IServiceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/service")
public class ServiceController {

	@Autowired
	IServiceService serviceService;

	@Autowired
	ServiceReporsitory serviceReporsitory;

	@Autowired
	PersonnelReporsitory personnelReporsitory;

	@Autowired
	ClientReporsitory clientReporsitory;

	@Autowired
	IPersonnelService personnelService;
	
	
	@Autowired
	IUserDetailsService userService;

	@PutMapping("/generationTs/{id}")
	@PreAuthorize("hasRole('update_gts_service')")
	public ResponseEntity<?> updateGenerationTS(@PathVariable("id") UUID id,
			@Valid @RequestBody GenerationTs generationTs) {
		log.info("[ SERVICE CONTROLLER ] ~ [ UPDATE GENERATION TS BY ID ]");

		try {
			if (!serviceReporsitory.existsById(id)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id !"));
			}
			if (Outils.checkTypeGenerationTS(generationTs.getSTypeGenerationTs()) == null) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found type generation !"));
			}
			if (!Outils.checkParametrageGenerationTS(generationTs)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Parametrage incorrecte !"));
			}
			serviceService.updateGenerationTS(id, generationTs);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/services")
	@PreAuthorize("hasRole('find_services')")
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		log.info("[ SERVICE CONTROLLER ] ~ [ FIND ALL SERVICE ]");

		try {
			Page<PKService> services = serviceService.findAll(PageRequest.of(page, size));

			return new ResponseEntity<>(services, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('findOne_service')")
	public ResponseEntity<?> getOne(@PathVariable("id") UUID id) {

		log.info("[ SERVICE CONTROLLER ] ~ [ GET SERVICE BY ID ]");
		try {
			if (!serviceReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id  !"));
			}
			PKService user = serviceService.findServiceById(id);

			return new ResponseEntity<>(user, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@PutMapping("/assignResponsable/{id}")
	@PreAuthorize("hasRole('assign_responsable_services')")
	public ResponseEntity<?> aasignResponsable(@PathVariable("id") UUID id, @RequestBody List<UUID> idServices) {
		log.info("[ SERVICE CONTROLLER ] ~ [ ASSIGN RESPONSABLE BY ID TO SERVICES]");
		try {
			if (!personnelReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id !"));

			}

			for (UUID idService : idServices) {
				if (!serviceReporsitory.existsById(idService)) {

					return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id !"));
				}
			}
			serviceService.assignResponsableService(idServices, id);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/assignSuperviseur/{id}")
	@PreAuthorize("hasRole('assign_superviseur_services')")
	public ResponseEntity<?> aasignSuperviseur(@PathVariable("id") UUID id, @RequestBody List<UUID> idServices) {
		log.info("[ SERVICE CONTROLLER ] ~ [ ASSIGN SUPERVISEUR BY ID TO SERVICES]");
		try {
			if (!personnelReporsitory.existsById(id)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id !"));

			}
			for (UUID idService : idServices) {
				if (!serviceReporsitory.existsById(idService)) {

					return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id !"));
				}
			}
			serviceService.assignSuperviseurService(idServices, id);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/services/responsable/{id}")
	@PreAuthorize("hasRole('find_services_responsable_superviseur')")
	public ResponseEntity<?> findServicesByResponsable(@PathVariable("id") UUID idUser) {

		log.info("[ SERVICE CONTROLLER ] ~ [ FIND  SERVICES BY ID RESPONSABLE ]");

		try {

			Set<PKDepartement> departements = serviceService.findDepartementsByResponsable(idUser);
			PKUser user=userService.findOne(idUser);
//			PKDepartement d=user.getPersonnel().getService().getClient().getDepartement();
//			PKClient c=user.getPersonnel().getService().getClient();
			PKService s=user.getPersonnel().getService();
			List<DepartementResponse> depts = new ArrayList<DepartementResponse>();
//			DepartementResponse deptU=new DepartementResponse();
//			deptU.setShortNameDepartement(d.getShortNameDepartement());
//			deptU.setId(d.getId());
//			depts.add(deptU);
//			Client cltU=new Client();
//			cltU.setId(c.getId());
//			cltU.setShortNameClient(c.getShortNameClient());
			ServiceResponse servRep=new ServiceResponse();
			servRep.setId(s.getId());
			servRep.setNameService(s.getNameService());
			for (PKDepartement departement : departements) {

				DepartementResponse dept = new DepartementResponse();
				dept.setId(departement.getId());
				dept.setShortNameDepartement(departement.getShortNameDepartement());

				List<Client> clients = new ArrayList<Client>();
				
				for (PKClient client : departement.getClients()) {
					if (!client.getServices().isEmpty()) {
						Client clt = new Client();
						clt.setId(client.getId());
						clt.setShortNameClient(client.getShortNameClient());

						List<ServiceResponse> services = new ArrayList<ServiceResponse>();
						
						for (PKService service : client.getServices()) {
							ServiceResponse ser = new ServiceResponse();
							ser.setId(service.getId());
							ser.setNameService(service.getNameService());
							services.add(ser);
							if(client.equals(user.getPersonnel().getService().getClient())) {
								if(!services.contains(servRep)) {
							services.add(servRep);
								}
							}
						}
						
						clt.setServices(services);
						clients.add(clt);
					}

					dept.setClients(clients);
					depts.add(dept);
				}
			}
			
			Set<DepartementResponse> results = new HashSet<DepartementResponse>(depts);

			return new ResponseEntity<>(results, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@GetMapping("/services/superviseur/{id}")
//	@PreAuthorize("hasRole('find_services_superviseur')")
//	public ResponseEntity<?> findServicesBySuperviseur(@PathVariable("id") String id) {
//
//		log.info("[ SERVICE CONTROLLER ] ~ [ FIND  SERVICES BY ID SUPERVISEUR ]");
//
//		try {
//			Set<PKDepartement> departements = serviceService.findDepartementsBySuperviseur(id);
//
//			return new ResponseEntity<>(departements, HttpStatus.OK);
//
//		} catch (Exception ex) {
//			log.error("ERROR : ", ex.getMessage());
//			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//
//		}
//
//	}

	@PutMapping("/suprimerResp/{id}")
	@PreAuthorize("hasRole('delete_responsable_service')")
	public ResponseEntity<?> deleteResponsableService(@PathVariable("id") UUID idService) {
		log.info("[ SERVICE CONTROLLER ] ~ [ DELETE RESPONSABLE WITH SERVICE]");

		try {
			if (!serviceReporsitory.existsById(idService)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id !"));
			}
			serviceService.deleteResponsable(idService);
			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);
		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/suprimerSuper/{id}")
	@PreAuthorize("hasRole('delete_superviseur_service')")
	public ResponseEntity<?> deleteSuperviseurService(@PathVariable("id") UUID idService) {
		log.info("[ SERVICE CONTROLLER ] ~ [ DELETE SUPERVISEUR WITH SERVICE]");

		try {
			if (!serviceReporsitory.existsById(idService)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id !"));
			}
			serviceService.deleteSuperviseur(idService);
			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);
		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("client/{id}")
	@PreAuthorize("hasRole('services_by_client')")
	public ResponseEntity<?> getServicesByClient(@PathVariable("id") UUID id,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ SERVICE CONTROLLER ] ~ [ GET SERVICES BY ID CLIENT PAGE ]");
		try {
			if (!clientReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found client with id  !"));
			}

			Page<PKService> services = serviceService.findServicesByClient(id, PageRequest.of(page, size));

			return new ResponseEntity<>(services, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@GetMapping("client/list/{id}")
	@PreAuthorize("hasRole('services_by_idClient')")
	public ResponseEntity<?> getServicesByIdClient(@PathVariable("id") UUID id) {

		log.info("[ SERVICE CONTROLLER ] ~ [ GET SERVICES BY ID CLIENT LIST]");
		try {
			if (!clientReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found client with id  !"));
			}
			List<PKService> services = serviceService.findServiceByIdClient(id);

			return new ResponseEntity<>(services, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@PutMapping("/activeResponsable")
	@PreAuthorize("hasRole('active_resp_service')")
	public ResponseEntity<?> validationResponsable(@Valid @RequestBody ParametrageResponsable parametre) {
		log.info("[ SERVICE CONTROLLER ] ~ [ VALIDATION RESPONSABLE ]");

		try {
			
			if (Outils.checkIOrganisme(parametre.organisme) == null) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Parametrage incorrecte !"));
			}
			serviceService.activeResponsable(parametre);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
}
