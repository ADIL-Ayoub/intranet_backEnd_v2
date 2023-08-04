package com.procheck.intranet.controllers;


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
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.ClientReporsitory;
import com.procheck.intranet.repository.DepartementReporsitory;
import com.procheck.intranet.services.IClientService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/client")
public class ClientController {

	@Autowired
	IClientService clientService;

	@Autowired
	ClientReporsitory clientReporsitory;
	
	@Autowired
	DepartementReporsitory departementReporsitory;

	@PutMapping("/generationTs/{id}")
	@PreAuthorize("hasRole('update_gts_client')")
	public ResponseEntity<?> updateGenerationTS(@PathVariable("id") UUID id,@Valid @RequestBody GenerationTs generationTs) {
		log.info("[ CLIENT CONTROLLER ] ~ [ UPDATE GENERATION TS BY ID ]");

		try {
			if (!clientReporsitory.existsById(id)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found client with id !"));
			}
			if(Outils.checkTypeGenerationTS(generationTs.getSTypeGenerationTs()) == null) {
				
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found type generation !"));
			}
			if(!Outils.checkParametrageGenerationTS(generationTs)) {
				
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Parametrage incorrecte !"));
			}
			
			clientService.updateGenerationTS(id,generationTs);

			return new ResponseEntity<>(new MessageResponse("operation registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	@GetMapping("/clients")
	@PreAuthorize("hasRole('find_clients')")
	public ResponseEntity<?> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ CLIENT CONTROLLER ] ~ [ FIND ALL CLIENTS ]");

		try {
			
			Page<PKClient> clients=clientService.findAll(PageRequest.of(page, size));
			
			return new ResponseEntity<>(clients, HttpStatus.OK);
		
		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('findOne_client')")
	public ResponseEntity<?> getOne(@PathVariable("id") UUID id) {

		log.info("[ CLIENT CONTROLLER ] ~ [ GET CLIENT BY ID ]");
		try {
			if (!clientReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found client with id  !"));
			}
			PKClient client = clientService.findClientById(id);

			return new ResponseEntity<>(client, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}

	@GetMapping("departement/{id}")
	@PreAuthorize("hasRole('clients_by_departement')")
	public ResponseEntity<?> getClientsByDepartement(@PathVariable("id") UUID id,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ CLIENT CONTROLLER ] ~ [ GET CLIENTS BY ID DEPARTEMENT]");
		try {
			if (!departementReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found departement with id  !"));
			}
			
			Page<PKClient> clients = clientService.findClientsByDepartement(id,PageRequest.of(page, size));

			return new ResponseEntity<>(clients, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}
}
