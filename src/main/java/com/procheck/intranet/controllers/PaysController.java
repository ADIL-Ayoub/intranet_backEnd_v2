package com.procheck.intranet.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKPays;
import com.procheck.intranet.payload.request.AffectationPays;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.PaysReporsitory;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.services.IPaysService;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/pays")
public class PaysController {

	@Autowired
	ServiceReporsitory serviceReporsitory;

	@Autowired
	IPaysService paysService;
	
	@Autowired
	PaysReporsitory paysReporsitory;
	
	@PutMapping("/idServicesPays")
	@PreAuthorize("hasRole('pays_employees')")
	public ResponseEntity<?> affectationPaysEmployee(@RequestBody AffectationPays modele) {
		log.info("[ USER CONTROLLER ] ~ [ UPDATE EMAIL ROLES USER BY ID ]");
		try {
			
		if (!paysReporsitory.existsById(modele.idPays)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found pays with id !"));
		}
		for (UUID idService : modele.idServies) {
			if (!serviceReporsitory.existsById(idService)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id  !"));
			}
		}
		
		paysService.affecationEmployees(modele);

			return new ResponseEntity<>(new MessageResponse("pays registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@GetMapping("/all")
	@PreAuthorize("hasRole('find_all_pays')")
	public ResponseEntity<?> findAll() {

		log.info("[ PAYS CONTROLLER ] ~ [ FIND ALL PAYS ]");

		try {
			List<PKPays> pays = paysService.findAll();

			return new ResponseEntity<>(pays, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
}
