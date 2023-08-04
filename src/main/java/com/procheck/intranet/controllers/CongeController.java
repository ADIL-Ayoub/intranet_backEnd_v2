package com.procheck.intranet.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKConge;
import com.procheck.intranet.models.PKDemande;
import com.procheck.intranet.models.PKHoraire;
import com.procheck.intranet.models.PKJoureFerie;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKTypeDemande;
import com.procheck.intranet.outils.Outils;

import com.procheck.intranet.payload.request.InfosPersonne;
import com.procheck.intranet.services.IDemandeService;
import com.procheck.intranet.services.IJourFerieService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.ISemaineTravailService;
import com.procheck.intranet.services.ITypeDemandeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/conge")
public class CongeController {
	
	
	@Autowired
	IPersonnelService personnelService;
	
	@Autowired
	IJourFerieService jourFerieService;
	
	@Autowired
	ISemaineTravailService semaineTravailService;
	
	@Autowired
	IDemandeService demandeService;
	
	@Autowired
	ITypeDemandeService typeDemandeService;
	
	
	@GetMapping("/chargementInfo/{idUser}")
	@PreAuthorize("hasRole('find_infos_employee')")
	public ResponseEntity<?> chargementInfos(@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "idBeneficiaire", required = false, defaultValue = "") UUID idBeneficiaire){
	try {
		
		PKPersonnel beneficiare=personnelService.findPersonnelById(idBeneficiaire);
		List<PKJoureFerie> joureFeries=jourFerieService.findJoursFeriesByPaysId(beneficiare.getPkPays().getId());
		List<PKHoraire> jourTravail=semaineTravailService.findByIdPersonnel(idBeneficiaire).getHoraires();
		PKTypeDemande typeDemande=typeDemandeService.findTypeDemandeByCode("DC");
		List<PKDemande> demandes=demandeService.findByPersonnelAndTypedemande(idBeneficiaire,typeDemande.getId());
		List<PKConge> conges=new ArrayList<PKConge>();
		for (PKDemande demande : demandes) {
			
			conges.addAll(demande.getConges());
		}
		InfosPersonne infos=Outils.MapToInfosPersonne(name,beneficiare,joureFeries,jourTravail,conges);
		
		return new ResponseEntity<>(infos, HttpStatus.OK);
 
	} catch (Exception ex) {
		log.error("ERROR : ", ex.getMessage());
		return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

	}
	}

	
}
