package com.procheck.intranet.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.procheck.intranet.services.IHoraireService;
import com.procheck.intranet.services.ISemaineTravailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/horaire")
public class HoraireController {

	@Autowired
	IHoraireService horaireService;
	
	@Autowired
	ISemaineTravailService semaineTravailService;
	
	
	@GetMapping("/horaireByEmployeeAndJour/{id}")
	@PreAuthorize("hasRole('find_horaire_jour')")
	public ResponseEntity<?> findAll(@RequestParam(value = "date", required = false, defaultValue = "") String date,
			@RequestParam(value = "jour", required = false, defaultValue = "") String jour,
			@RequestParam(value = "periode", required = false, defaultValue = "") String periode,
			@RequestParam(value = "party", required = false, defaultValue = "") String party) {

		log.info("[ HORAIRE CONTROLLER ] ~ [ FIND HORAIRE BY ID PERSONNEL AND DATE ]");

		try {
			
			 
			
			return new ResponseEntity<>( HttpStatus.OK);
		
		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
}
