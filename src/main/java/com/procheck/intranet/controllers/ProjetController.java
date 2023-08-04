package com.procheck.intranet.controllers;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKProjet;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.AffectationProjet;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.Projet;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.ProjetReporsitory;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.services.IProjetService;
import com.procheck.intranet.services.IServiceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/projet")
public class ProjetController {

	@Autowired
	IProjetService projetService;

	@Autowired
	ProjetReporsitory projetReporsitory;
	
	@Autowired
	ServiceReporsitory serviceReporsitory;
	
	@Autowired
	IServiceService serviceService;

	@PutMapping("/generationTs/{id}")
	@PreAuthorize("hasRole('update_gts_projet')")
	public ResponseEntity<?> updateGenerationTS(@PathVariable("id") UUID id,@Valid @RequestBody GenerationTs generationTs) {
		log.info("[ CLIENT CONTROLLER ] ~ [ UPDATE GENERATION TS BY ID ]");

		try {
			if (!projetReporsitory.existsById(id)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found projet with id !"));
			}
			if (Outils.checkTypeGenerationTS(generationTs.getSTypeGenerationTs()) == null) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found type generation !"));
			}
			if (!Outils.checkParametrageGenerationTS(generationTs)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Parametrage incorrecte !"));
			}

			projetService.updateGenerationTS(id, generationTs);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	
	@PostMapping("/add")
	@PreAuthorize("hasRole('add_projet')")
	public ResponseEntity<?> add(@Valid @RequestBody Projet projet){
		log.info("[ PROJET CONTROLLER ] ~ [ CREATE PROJET ]");
	try {
		if(projetReporsitory.existsByCodeProjet(projet.getCodeProjet())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: projet is already in use!"));
		}
		if(!Outils.checkPerimetre(projet.getPerimeter())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: perimetre invalide !"));
		}
		
		projetService.save(projet);
		
        return new ResponseEntity<>(new MessageResponse("projet registered successfully!"), HttpStatus.OK);
		
	} catch (Exception ex) {
		log.error(ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	}

	
	@PutMapping("/assign/{idUser}")
	@PreAuthorize("hasRole('assign_service_projets')")
	public ResponseEntity<?> aasignProjets(@PathVariable("idUser") UUID idUser,@RequestBody  AffectationProjet affectationProjet) {
		log.info("[ PROJET CONTROLLER ] ~ [ ASSIGN PROJET TO SERVICES ]");
	try {
		if(!serviceReporsitory.existsById(affectationProjet.getIdService())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id !"));
		}
		
		serviceService.assignProjetsToService(affectationProjet.getIdService(),affectationProjet.getProjets());
		
		return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);
		
	} catch (Exception ex) {
		   log.error(ex.getMessage());
           return new ResponseEntity<>(ex.getMessage() ,HttpStatus.INTERNAL_SERVER_ERROR);
       }
	}
	
	@GetMapping("/findByCode/{idService}")
	@PreAuthorize("hasRole('find_by_code_service')")
	public ResponseEntity<?> findByCodeAndService(@PathVariable("idService") UUID idService,
			@RequestParam(value = "code", required = false, defaultValue = "") String code) {

		log.info("[ PROJET CONTROLLER ] ~ [ GET PROJET BY SERVICE AND CODE ]");
		try {

			if(!serviceReporsitory.existsById(idService)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id !"));
			}
			
			List<PKProjet> projetes = projetService.findByServiceAndCodeProjet(idService, code);

			return new ResponseEntity<>(projetes, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	@GetMapping("/findProjets/{idService}")
	@PreAuthorize("hasRole('find_projets_service')")
	public ResponseEntity<?> findByService(@PathVariable("idService") UUID idService) {

		log.info("[ PROJET CONTROLLER ] ~ [ GET PROJETS BY SERVICE ]");
		try {
			if(!serviceReporsitory.existsById(idService)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id !"));
			}
			List<PKProjet> projetes = projetService.findProjetsByService(idService);

			return new ResponseEntity<>(projetes, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
}
