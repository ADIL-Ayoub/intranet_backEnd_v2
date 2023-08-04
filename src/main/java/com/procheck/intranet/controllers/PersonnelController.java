package com.procheck.intranet.controllers;

import java.util.List;
import java.util.Objects;
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

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.PersonnelFilter;
import com.procheck.intranet.payload.request.PersonnelFilterByService;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.ClientReporsitory;
import com.procheck.intranet.repository.DepartementReporsitory;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.repository.ProjetReporsitory;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.repository.UserRepository;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.IPersonnelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/personnel")
public class PersonnelController {

	@Autowired
	IPersonnelService personnelService;

	@Autowired
	PersonnelReporsitory personnelReporsitory;

	@Autowired
	DepartementReporsitory departementReporsitory;

	@Autowired
	ClientReporsitory clientReporsitory;

	@Autowired
	ProjetReporsitory projetReporsitory;

	@Autowired
	ServiceReporsitory serviceReporsitory;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	IUserDetailsService userService;
	
	
	

	@GetMapping("/personnels/byFilter")
	@PreAuthorize("hasRole('find_personnels_cin_nom_prenom')")
	public ResponseEntity<?> findPersonnelByFilterNonAffecte(
			@RequestParam(value = "cin", required = false, defaultValue = "") String cin,
			@RequestParam(value = "nom", required = false, defaultValue = "") String nom,
			@RequestParam(value = "prenom", required = false, defaultValue = "") String prenom,
			@RequestParam(value = "poste", required = false, defaultValue = "") String poste,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ GET PERSONNEL BY CIN OR NOM OR PRENOM NON AFFECTE ]");

		try {
			boolean affectation = false;
			PersonnelFilter personne=new PersonnelFilter(cin.toUpperCase(),nom.toUpperCase(),prenom.toUpperCase(),poste.toUpperCase(),affectation);

			Page<PKPersonnel> personnels = personnelService.findPersonnelByCinOrNameOrPrenomeOrPoste(personne, size,
					page);

			return new ResponseEntity<>(personnels, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	@GetMapping("/personnels/byFilterAll")
	@PreAuthorize("hasRole('find_personnels_by_filter')")
	public ResponseEntity<?> findPersonnelByFilter(
			@RequestParam(value = "cin", required = false, defaultValue = "") String cin,
			@RequestParam(value = "nom", required = false, defaultValue = "") String nom,
			@RequestParam(value = "prenom", required = false, defaultValue = "") String prenom,
			@RequestParam(value = "poste", required = false, defaultValue = "") String poste,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ GET PERSONNEL BY CIN OR NOM OR PRENOM ]");

		try {
			
			PersonnelFilter personne=new PersonnelFilter(cin.toUpperCase(),nom.toUpperCase(),prenom.toUpperCase(),poste.toUpperCase());
			
			Page<PKPersonnel> personnels = personnelService.findPersonnelByFilterAll(personne, size,page);

			return new ResponseEntity<>(personnels, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	
	@GetMapping("/personnels/byFilterAndService")
	@PreAuthorize("hasRole('find_personnels_by_filter_and_services')")
	public ResponseEntity<?> findPersonnelByFilterAndService(
			@RequestParam(value = "services", required = false, defaultValue = "") List<UUID> services, 
			@RequestParam(value = "cin", required = false, defaultValue = "") String cin,
			@RequestParam(value = "nom", required = false, defaultValue = "") String nom,
			@RequestParam(value = "prenom", required = false, defaultValue = "") String prenom,
			@RequestParam(value = "matrucule", required = false, defaultValue = "") String matrucule,
			@RequestParam(value = "post", required = false, defaultValue = "") String post,
			@RequestParam(value = "projet", required = false, defaultValue = "") boolean projet,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ GET PERSONNEL BY CIN OR NOM OR PRENOM OR MATRUCULE OR POSTE ]");

		try {

				PersonnelFilterByService filter=new PersonnelFilterByService(services, cin.toUpperCase(), nom.toUpperCase(), prenom.toUpperCase(), matrucule.toUpperCase(),post.toUpperCase(),projet);
				List<PKPersonnel> personnels=personnelService.findPersonnelByServicesAndFilter(filter);
				
			return new ResponseEntity<>(personnels,HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	@GetMapping("/personnels")
	@PreAuthorize("hasRole('find_personnels')")
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ FIND ALL PERSONNELS ]");

		try {

			return new ResponseEntity<>(personnelService.findAll(PageRequest.of(page, size)), HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('findOne_personnel')")
	public ResponseEntity<?> getOne(@PathVariable("id") UUID id) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ GET PERSONNEL BY ID ]");
		try {
			if (!personnelReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id  !"));
			}
			PKPersonnel personnel = personnelService.findPersonnelById(id);

			return new ResponseEntity<>(personnel, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@GetMapping("departement/{id}")
	@PreAuthorize("hasRole('find_personnel_by_departement')")
	public ResponseEntity<?> getPersonnelsByDepartement(@PathVariable("id") UUID id) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ GET PERSONNELS BY ID DEPARTEMENT ]");
		try {
			if (!departementReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found departement with id  !"));
			}
			List<PKPersonnel> personnels = personnelService.findPersonnelsByDepartement(id);

			return new ResponseEntity<>(personnels, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@GetMapping("client/{id}")
	@PreAuthorize("hasRole('find_personnel_by_client')")
	public ResponseEntity<?> getPersonnelsByClient(@PathVariable("id") UUID id) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ GET PERSONNELS BY ID CLIENT ]");
		try {
			if (!clientReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found client with id  !"));
			}
			List<PKPersonnel> personnels = personnelService.findPersonnelsByClient(id);

			return new ResponseEntity<>(personnels, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@GetMapping("projet/{id}")
	@PreAuthorize("hasRole('find_personnel_by_projet')")
	public ResponseEntity<?> getPersonnelsByProjet(@PathVariable("id") UUID id) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ GET PERSONNELS BY ID PROJET ]");
		try {
			if (!projetReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found projet with id  !"));
			}
			List<PKPersonnel> personnels = personnelService.findPersonnelsByProjet(id);

			return new ResponseEntity<>(personnels, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@GetMapping("service/{id}")
	@PreAuthorize("hasRole('find_personnel_by_service')")
	public ResponseEntity<?> getPersonnelsByService(@PathVariable("id") UUID id) {

		log.info("[ PERSONNEL CONTROLLER ] ~ [ GET PERSONNELS BY ID SERVICE ]");
		try {
			if (!serviceReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id  !"));
			}
			List<PKPersonnel> personnels = personnelService.findPersonnelsByService(id);

			return new ResponseEntity<>(personnels, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@PutMapping("/assignUserToPersonnel/{idPersonnel}/{idUser}")
	@PreAuthorize("hasRole('assign_user_personnel')")
	public ResponseEntity<?> aasignPersonnelUser(@PathVariable("idPersonnel") UUID idPersonnel,
			@PathVariable("idUser") UUID idUser,@RequestParam boolean affecte  ) {
		log.info("[ ROLE CONTROLLER ] ~ [ ASSIGN PRIVILEGE BY ID ]");

		log.info("affecte :"+affecte);
		try {
			if (!personnelReporsitory.existsById(idPersonnel)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id !"));

			}
			if (!userRepository.existsById(idUser)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with id !"));

			}
			PKUser user = userRepository.findById(idUser).get();
			
			
			if (affecte == true) {

				user.setAffecte(true);
				userRepository.save(user);
				personnelService.assignUser(idPersonnel, user);

				return new ResponseEntity<>(new MessageResponse("User assigned successfully!"), HttpStatus.OK);
			} else {

				user.setAffecte(false);
				userRepository.save(user);
				personnelService.deleteUser(idPersonnel, user);
				return new ResponseEntity<>(new MessageResponse("User ignored  successfully!"), HttpStatus.OK);

			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/deleteUserToPersonnel/{idPersonnel}/{idUser}")
	@PreAuthorize("hasRole('delete_user_personnel')")
	public ResponseEntity<?> deletePersonnelUser(@PathVariable("idPersonnel") UUID idPersonnel,
			@PathVariable("idUser") UUID idUser) {
		log.info("[ ROLE CONTROLLER ] ~ [ ASSIGN PRIVILEGE BY ID ]");
		try {
			if (!personnelReporsitory.existsById(idPersonnel)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id !"));

			}
			if (!userRepository.existsById(idUser)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with id !"));

			}
			PKUser user = userRepository.findById(idUser).get();
			user.setAffecte(false);
			userRepository.save(user);
			personnelService.deleteUser(idPersonnel, user);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/generationTs/{id}")
	@PreAuthorize("hasRole('update_gts_employee')")
	public ResponseEntity<?> updateGenerationTS(@PathVariable("id") UUID id,
			@Valid @RequestBody GenerationTs generationTs) {
		log.info("[ PERSONNEL CONTROLLER ] ~ [ UPDATE GENERATION TS BY ID ]");

		try {
			if (!personnelReporsitory.existsById(id)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id !"));
			}
			if (Outils.checkTypeGenerationTS(generationTs.getSTypeGenerationTs()) == null) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found type generation !"));
			}
			if (!Outils.checkParametrageGenerationTS(generationTs)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Parametrage incorrecte !"));
			}
			personnelService.updateGenerationTS(id, generationTs);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PutMapping("/affectationSuperieur/{idPersonne}")
	@PreAuthorize("hasRole('affectation_superieur')")
	public ResponseEntity<?> affectationSuperieur(@PathVariable("id") UUID idPersonne,
			@PathVariable("idSuperieur") UUID idSuperieur) {
		log.info("[ PERSONNEL CONTROLLER ] ~ [ UPDATE GENERATION TS BY ID ]");

		try {
			if (!personnelReporsitory.existsById(idSuperieur) || !personnelReporsitory.existsById(idPersonne)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id !"));
			}
			
			personnelService.updateEmployee(idPersonne, idSuperieur);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	
	@PutMapping("/affectationSuperEmp/{idUser}")
	@PreAuthorize("hasRole('affectation_super_emp')")
	public ResponseEntity<?> affectationSuperEmp(@PathVariable("idUser") UUID idUser,
			@RequestParam("idEmp") UUID idEmp) {
		log.info("[ PERSONNEL CONTROLLER ] ~ [ AFFECTATION SUPER EMP BY ID EMP ]");

		try {
			if (!personnelReporsitory.existsById(idEmp)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id !"));
			}
			
			PKUser user=personnelService.findPersonnelById(idEmp).getUser();
			
			if(Objects.equals(user, null)) {
				
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found user with personne !"));
			}else {
				
				userService.assigneRoleEmpGTS(user.getId());
			}
		
			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	
	
}
