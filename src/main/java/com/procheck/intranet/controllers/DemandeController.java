package com.procheck.intranet.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.validation.Valid;

import com.procheck.intranet.services.specifications.MailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

import com.procheck.intranet.models.PKConge;
import com.procheck.intranet.models.PKDemande;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKTypeConge;
import com.procheck.intranet.models.PKTypeDemande;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.outils.DateValidator;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.Conge;
import com.procheck.intranet.payload.request.Demande;
import com.procheck.intranet.payload.request.DemandeFilter;
import com.procheck.intranet.payload.request.MesConges;
import com.procheck.intranet.payload.request.MesDocuments;
import com.procheck.intranet.payload.request.PersonnelFilter;
import com.procheck.intranet.payload.request.Timesheet;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.CongeReporsitory;
import com.procheck.intranet.repository.DemandeReporsitory;
import com.procheck.intranet.repository.HoraireRepository;
import com.procheck.intranet.repository.JourFerieReporsitory;
import com.procheck.intranet.repository.TimesheetReporsitory;
import com.procheck.intranet.repository.TypeCongeReporsitory;
import com.procheck.intranet.repository.UserRepository;
import com.procheck.intranet.security.services.IRoleService;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.ICongeService;
import com.procheck.intranet.services.IDemandeService;
import com.procheck.intranet.services.IHoraireService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.ITypeCongeService;
import com.procheck.intranet.services.ITypeDemandeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/demande")
public class DemandeController {

	@Autowired
	CongeReporsitory congeReporsitory;

	@Autowired
	DemandeReporsitory demandeReporsitory;

	@Autowired
	TimesheetReporsitory timesheetReporsitory;

	@Autowired
	ICongeService congeService;

	@Autowired
	IDemandeService demandeSevice;

	@Autowired
	HoraireRepository horaireRepository;

	@Autowired
	IPersonnelService personnelService;

	@Autowired
	ITypeCongeService typeCongeService;

	@Autowired
	JourFerieReporsitory jourFerieReporsitory;

	@Autowired
	IUserDetailsService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	IRoleService roleService;

	@Autowired
	IHoraireService horaireService;

	@Autowired
	ITypeDemandeService typeDemandeService;

	@Autowired
	MailSenderService mailSenderService;

/*
	@PostMapping("/add/{idUser}")
	@PreAuthorize("hasRole('add_demande_conge')")
	public ResponseEntity<?> add(@PathVariable("idUser") UUID idUser, @Valid @RequestBody Conge conge) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ CREATE DEMANDE CONGE ]");
		try {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate startDate = LocalDate.parse(conge.getDateDebut().substring(0,10), formatter);
			LocalDate finDate = LocalDate.parse(conge.getDateReprise().substring(0,10), formatter);
			PKUser user = userService.findOne(idUser);
			PKPersonnel employee = user.getPersonnel();
			if (congeReporsitory.existsByDateDebutAndDateRepriseAndDemande_Personnel_id(startDate, finDate,
					employee.getId())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la demande déja exist !!"));
			}
			List<LocalDate> days = Outils.getDatesBetweenConge(conge.getDateDebut().substring(0,10), conge.getDateReprise().substring(0,10));
			List<LocalDate> daysConge = new ArrayList<LocalDate>();

			if (!(employee.getFNbJourConge() > 0)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: pas solde conge !"));
			}

			for (LocalDate date : days) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, employee.getId())) {

					return ResponseEntity.badRequest()
							.body(new MessageResponse("Error: la date : " + date + " déja exist dans la table TS !"));
				}

				String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
				log.info("DAY NAME :" + str);

				boolean jourferie = jourFerieReporsitory.existsByPays_idAndDateJoureFerie(employee.getPkPays().getId(),
						date);
				boolean jourTravail = horaireRepository.existsByJourIgnoreCaseAndSemaineTravails_Id(str,
						employee.getSemaineTravail().getId());

				if (!jourferie && jourTravail) {
					daysConge.add(date);
				}
			}
			if (!(employee.getFNbJourConge() > daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: solde conge "
						+ employee.getFNbJourConge() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKTypeConge typeConge = typeCongeService.findTypeCongeById(conge.getTypeConge());

			if (!(typeConge.getMax() >= daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: nombre max de conge "
						+ typeConge.getMax() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKDemande demande = new PKDemande();
			demande.setDDateCreation(LocalDate.now());
			demande.setCodeDemandeur(userService.findOne(idUser).getUsername());

			demande.setPersonnel(employee);
			demande.setStatus("enregistré");

			demande.setTypedemande(typeDemandeService.findTypeDemandeById(conge.typeDemande));

			PKConge dconge = new PKConge();
			dconge.setName(conge.getTitre());
			dconge.setDescription(conge.getDescription());
			dconge.setDateDebut(startDate);
			dconge.setDateReprise(finDate);
			dconge.setNombreJour(daysConge.size());
			dconge.setTypeConge(typeConge);
			dconge.setStatus("crée");
			dconge.setDemande(demande);
			congeService.save(dconge);

			return new ResponseEntity<>(new MessageResponse("la demande de congé sont bien enregistrées !"),
					HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
*/

	@PostMapping("/add/{idUser}")
	@PreAuthorize("hasRole('add_demande_conge')")
	public ResponseEntity<?> add(@PathVariable("idUser") UUID idUser, @Valid @RequestBody Conge conge) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ CREATE DEMANDE CONGE ]");
		try {
			//System.out.println(conge);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate startDate = LocalDate.parse(conge.getDateDebut().substring(0,10), formatter);
			LocalDate finDate = LocalDate.parse(conge.getDateReprise().substring(0,10), formatter);
			PKUser user = userService.findOne(idUser);
			PKPersonnel employee = user.getPersonnel();
			//ihistoriqueService.SaveHistorique(idUser," Ajouter demande congé "+employee.getSNom());
			if (congeReporsitory.existsByDateDebutAndDateRepriseAndDemande_Personnel_id(startDate, finDate,
					employee.getId())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la demande déja exist !!"));
			}

			List<LocalDate> days = Outils.getDatesBetweenConge(conge.getDateDebut().substring(0,10), conge.getDateReprise().substring(0,10));
			List<LocalDate> daysConge = new ArrayList<LocalDate>();
			if (!(employee.getFNbJourConge() > 0)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: pas solde conge !"));
			}
			for (LocalDate date : days) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, employee.getId())) {

					return ResponseEntity.badRequest().body(
							new MessageResponse("Error: la date : " + date + " déja exist dans la table TS !"));
				}

				String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
				log.info("DAY NAME :" + str);
				boolean jourferie = jourFerieReporsitory
						.existsByPays_idAndDateJoureFerie(employee.getPkPays().getId(), date);
				boolean jourTravail = horaireRepository.existsByJourIgnoreCaseAndSemaineTravails_Id(str,
						employee.getSemaineTravail().getId());
				if (!jourferie && jourTravail) {
					daysConge.add(date);
				}
			}
			if(daysConge.size()<1){
				return ResponseEntity.badRequest().body(new MessageResponse("Vous avez choisie des jours fériés"));
			}
			if (!(employee.getFNbJourConge() > daysConge.size())) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: solde conge "
						+ employee.getFNbJourConge() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKTypeConge typeConge = typeCongeService.findTypeCongeById(conge.getTypeConge());
			if (!(typeConge.getMax() >= daysConge.size())) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: nombre max de conge "
						+ typeConge.getMax() + " inferieur nombre jous conge " + daysConge.size()));
			}
			PKDemande demande = new PKDemande();
			demande.setDDateCreation(LocalDate.now());
			demande.setCodeDemandeur(userService.findOne(idUser).getUsername());

			demande.setPersonnel(employee);
			demande.setStatus("enregistré");
			demande.setTypedemande(typeDemandeService.findTypeDemandeById(conge.typeDemande));
			PKConge dconge = new PKConge();
			dconge.setName(conge.getName());
			dconge.setDescription(conge.getDescription());
			dconge.setDateDebut(startDate);
			dconge.setDateReprise(finDate);
			dconge.setNombreJour(daysConge.size());
			dconge.setTypeConge(typeConge);
			dconge.setStatus("crée");
			dconge.setDemande(demande);
			congeService.save(dconge);

			return new ResponseEntity<>(new MessageResponse("la demande de congé est bien enregistrée !"),
					HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/create/{idUser}")
	@PreAuthorize("hasRole('create_demande_conge_user')")
	public ResponseEntity<?> addDemandeConge(@PathVariable("idUser") UUID idUser, @Valid @RequestBody Conge conge) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ CREATE DEMANDE CONGE ]");
		try {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate startDate = LocalDate.parse(conge.getDateDebut(), formatter);
			LocalDate finDate = LocalDate.parse(conge.getDateReprise(), formatter);

			if (congeReporsitory.existsByDateDebutAndDateRepriseAndDemande_Personnel_id(startDate, finDate,
					conge.getIdPersonne())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la demande déja exist !!"));
			}

			PKPersonnel employee = personnelService.findPersonnelById(conge.getIdPersonne());

			List<LocalDate> days = Outils.getDatesBetweenConge(conge.getDateDebut(), conge.getDateReprise());
			List<LocalDate> daysConge = new ArrayList<LocalDate>();

			if (!(employee.getFNbJourConge() > 0)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: pas solde conge !"));
			}

			for (LocalDate date : days) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, employee.getId())) {

					return ResponseEntity.badRequest()
							.body(new MessageResponse("Error: la date : " + date + " déja exist dans la table TS !"));
				}

				String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
				log.info("DAY NAME :" + str);

				boolean jourferie = jourFerieReporsitory.existsByPays_idAndDateJoureFerie(employee.getPkPays().getId(),
						date);
				boolean jourTravail = horaireRepository.existsByJourIgnoreCaseAndSemaineTravails_Id(str,
						employee.getSemaineTravail().getId());

				if (!jourferie && jourTravail) {
					daysConge.add(date);
				}
			}
			if (!(employee.getFNbJourConge() > daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: solde conge "
						+ employee.getFNbJourConge() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKTypeConge typeConge = typeCongeService.findTypeCongeById(conge.getTypeConge());

			if (!(typeConge.getMax() >= daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: nombre max de conge "
						+ typeConge.getMax() + " inferieur nombre jous conge " + daysConge.size()));
			}
			PKDemande demande = new PKDemande();
			demande.setDDateCreation(LocalDate.now());
			demande.setCodeDemandeur(userService.findOne(idUser).getUsername());

			demande.setPersonnel(employee);
			demande.setStatus("enregistré");

			demande.setTypedemande(typeDemandeService.findTypeDemandeById(conge.typeDemande));

			PKConge dconge = new PKConge();
			dconge.setName(conge.getName());
			dconge.setDescription(conge.getDescription());
			dconge.setDateDebut(startDate);
			dconge.setDateReprise(finDate);
			dconge.setNombreJour(daysConge.size());
			dconge.setTypeConge(typeConge);
			dconge.setStatus("crée");
			dconge.setDemande(demande);
			congeService.save(dconge);

			return new ResponseEntity<>(new MessageResponse("la demande de congé sont bien enregistrées !"),
					HttpStatus.OK);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/creeAndenvoyer/{idUser}")
	@PreAuthorize("hasRole('cree_and_envoyer_conge')")
	public ResponseEntity<?> envoyedemandeConge(@PathVariable("idUser") UUID idUser, @RequestBody Conge conge) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ CREE AND ENVOYER LES DEMANDES  ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate startDate = LocalDate.parse(conge.getDateDebut(), formatter);
			LocalDate finDate = LocalDate.parse(conge.getDateReprise(), formatter);

			if (congeReporsitory.existsByDateDebutAndDateRepriseAndDemande_Personnel_id(startDate, finDate,
					conge.getIdPersonne())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la demande déja exist !!"));
			}

			PKPersonnel employee = personnelService.findPersonnelById(conge.getIdPersonne());
			PKUser user = employee.getUser();

			List<LocalDate> days = Outils.getDatesBetweenConge(conge.getDateDebut(), conge.getDateReprise());
			List<LocalDate> daysConge = new ArrayList<LocalDate>();

			if (!(employee.getFNbJourConge() > 0)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: pas solde conge !"));
			}

			for (LocalDate date : days) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, employee.getId())) {

					return ResponseEntity.badRequest()
							.body(new MessageResponse("Error: la date : " + date + " déja exist dans la table TS !"));
				}

				String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
				log.info("DAY NAME :" + str);

				boolean jourferie = jourFerieReporsitory.existsByPays_idAndDateJoureFerie(employee.getPkPays().getId(),
						date);
				boolean jourTravail = horaireRepository.existsByJourIgnoreCaseAndSemaineTravails_Id(str,
						employee.getSemaineTravail().getId());

				if (!jourferie && jourTravail) {
					daysConge.add(date);
				}
			}
			if (!(employee.getFNbJourConge() > daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: solde conge "
						+ employee.getFNbJourConge() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKTypeConge typeConge = typeCongeService.findTypeCongeById(conge.getTypeConge());

			if (!(typeConge.getMax() >= daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: nombre max de conge "
						+ typeConge.getMax() + " inferieur nombre jous conge " + daysConge.size()));
			}
			PKDemande demande = new PKDemande();
			demande.setDDateCreation(LocalDate.now());
			demande.setCodeDemandeur(userService.findOne(idUser).getUsername());

			demande.setPersonnel(employee);
			demande.setTypedemande(typeDemandeService.findTypeDemandeById(conge.typeDemande));

			if (!Objects.equals(user, null)) {
				if (roleService.getNameRoleByUser(user).contains("RESPONSABLE")) {
					if (!Objects.equals(employee.getSuperieur(), null)) {
						log.info("Code super :" + employee.getSuperieur());
						log.info("demande super :" + demande.getCodeSup());
						demande.setCodeSup(employee.getSuperieur());
					} else {
						return ResponseEntity.badRequest()
								.body(new MessageResponse(" manque de supérieurs hiérarchiques "));
					}
				} else if (roleService.getNameRoleByUser(user).contains("SUPERVISEUR")) {

					if (!Objects.equals(employee.getService().getCodeResponsable(), null)) {
						demande.setCodeSup(employee.getService().getCodeResponsable());
					} else {
						return ResponseEntity.badRequest()
								.body(new MessageResponse(" manque de supérieurs hiérarchiques "));
					}
				} else {

					if (!Objects.equals(employee.getService().getCodeSuperviseur(), null)) {
						demande.setCodeSup(employee.getService().getCodeSuperviseur());
					} else {
						return ResponseEntity.badRequest()
								.body(new MessageResponse(" manque de supérieurs hiérarchiques "));
					}
				}
			} else {
				if (!Objects.equals(employee.getService().getCodeSuperviseur(), null)) {
					demande.setCodeSup(employee.getService().getCodeSuperviseur());
				} else {
					return ResponseEntity.badRequest()
							.body(new MessageResponse(" manque de supérieurs hiérarchiques "));
				}
			}

			PKConge dconge = new PKConge();
			dconge.setName(conge.getName());
			dconge.setDescription(conge.getDescription());
			dconge.setDateDebut(startDate);
			dconge.setDateReprise(finDate);
			dconge.setNombreJour(daysConge.size());
			dconge.setTypeConge(typeConge);
			dconge.setStatus("envoyé");
			dconge.setDemande(demande);
			congeService.save(dconge);

			demande.setStatus("envoyé pour validation");
			demandeSevice.save(demande);
			employee.setFNbJourConge(employee.getFNbJourConge() - daysConge.size());
			personnelService.save(employee);

			return new ResponseEntity<>(new MessageResponse("la demande de congé sont bien envoyé !"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/creeAndvalider/{idUser}")
	@PreAuthorize("hasRole('cree_and_valider_conge')")
	public ResponseEntity<?> validerdemandeConge(@PathVariable("idUser") UUID idUser, @RequestBody Conge conge) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ CREE DEMANDE AND VALIDE]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate startDate = LocalDate.parse(conge.getDateDebut(), formatter);
			LocalDate finDate = LocalDate.parse(conge.getDateReprise(), formatter);

			if (congeReporsitory.existsByDateDebutAndDateRepriseAndDemande_Personnel_id(startDate, finDate,
					conge.getIdPersonne())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la demande déja exist !!"));
			}

			PKPersonnel employee = personnelService.findPersonnelById(conge.getIdPersonne());
			PKUser user = employee.getUser();

			List<LocalDate> days = Outils.getDatesBetweenConge(conge.getDateDebut(), conge.getDateReprise());
			List<LocalDate> daysConge = new ArrayList<LocalDate>();

			if (!(employee.getFNbJourConge() > 0)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: pas solde conge !"));
			}

			for (LocalDate date : days) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, employee.getId())) {

					return ResponseEntity.badRequest()
							.body(new MessageResponse("Error: la date : " + date + " déja exist dans la table TS !"));
				}

				String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
				log.info("DAY NAME :" + str);

				boolean jourferie = jourFerieReporsitory.existsByPays_idAndDateJoureFerie(employee.getPkPays().getId(),
						date);
				boolean jourTravail = horaireRepository.existsByJourIgnoreCaseAndSemaineTravails_Id(str,
						employee.getSemaineTravail().getId());

				if (!jourferie && jourTravail) {
					daysConge.add(date);
				}
			}
			if (!(employee.getFNbJourConge() > daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: solde conge "
						+ employee.getFNbJourConge() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKTypeConge typeConge = typeCongeService.findTypeCongeById(conge.getTypeConge());

			if (!(typeConge.getMax() >= daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: nombre max de conge "
						+ typeConge.getMax() + " inferieur nombre jous conge " + daysConge.size()));
			}
			PKDemande demande = new PKDemande();
			demande.setDDateCreation(LocalDate.now());
			demande.setCodeDemandeur(userService.findOne(idUser).getUsername());
			demande.setPersonnel(employee);
			demande.setTypedemande(typeDemandeService.findTypeDemandeById(conge.typeDemande));
			demande.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
			demande.setStatus("validé");
			demande.setDDateDecisionSup(LocalDate.now());
			demande.setDecisionSup(user.getUsername());
			demandeSevice.save(demande);

			PKConge dconge = new PKConge();
			dconge.setName(conge.getName());
			dconge.setDescription(conge.getDescription());
			dconge.setDateDebut(startDate);
			dconge.setDateReprise(finDate);
			dconge.setNombreJour(daysConge.size());
			dconge.setTypeConge(typeConge);
			dconge.setStatus("validé");
			dconge.setDemande(demande);
			congeService.save(dconge);

			employee.setFNbJourConge(employee.getFNbJourConge() - daysConge.size());
			personnelService.save(employee);

			return new ResponseEntity<>(new MessageResponse("la demande de congé sont bien validé !"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/soldeConge/{idEmployee}")
	@PreAuthorize("hasRole('solde_conge')")
	public ResponseEntity<?> soldeCongeIntranet(@PathVariable("idEmployee") UUID idEmployee) {
		try {

			log.info("[ DEMANDE CONTROLLER ] ~ [ SOLDE CONGE]");

			PKPersonnel employee = personnelService.findPersonnelById(idEmployee);

			double solde = employee.getFNbJourConge();

			return new ResponseEntity<>(solde, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@GetMapping("/soldeCongeSage/{idEmployee}")
	@PreAuthorize("hasRole('solde_conge_sage')")
	public ResponseEntity<?> soldeCongeSAGE(@PathVariable("idEmployee") UUID idEmployee) {
		try {

			log.info("[ DEMANDE CONTROLLER ] ~ [ SOLDE CONGE SAGE]");

			PKPersonnel employee = personnelService.findPersonnelById(idEmployee);

			double solde = employee.getFNbRestConge();

			return new ResponseEntity<>(solde, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@GetMapping("/demandesEnregistre/{idUser}")
	@PreAuthorize("hasRole('mes_demandes_enregistre')")
	public ResponseEntity<?> mesDemandeEnregistre(@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "idTypeDmd", required = false, defaultValue = "") UUID idTypeDmd,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		try {

			log.info("[ DEMANDE CONTROLLER ] ~ [ FIND MES DEMANDES ENREGISTRE ]");

			PKUser user = userService.findOne(idUser);

			List<PKDemande> demandes = demandeSevice.findByCodeDemandeurAndTypedemandeAndStatus(user.getUsername(),
					idTypeDmd, "crée");

			if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DC")) {

				List<MesConges> conges = Outils.MapDemandeToConges(demandes);
				PagedListHolder<MesConges> listHolder = new PagedListHolder<MesConges>(conges);

				listHolder.setPageSize(size);
				listHolder.setPage(page);

				Page<MesConges> pages = new PageImpl<MesConges>(listHolder.getPageList(),
						PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), conges.size());
				return new ResponseEntity<>(pages, HttpStatus.OK);

			} else if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DDA")) {

				List<MesDocuments> documents = Outils.MapDemandeToDocument(demandes);
				PagedListHolder<MesDocuments> listHolder = new PagedListHolder<MesDocuments>(documents);

				listHolder.setPageSize(size);
				listHolder.setPage(page);

				Page<MesDocuments> pages = new PageImpl<MesDocuments>(listHolder.getPageList(),
						PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), documents.size());
				return new ResponseEntity<>(pages, HttpStatus.OK);
			}

			return ResponseEntity.badRequest().body(new MessageResponse(" aucun demande trouvé !! "));

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@PutMapping("/envoyer/{idUser}")
	@PreAuthorize("hasRole('envoyer_demande_conge')")
	public ResponseEntity<?> envoyeConge(@PathVariable("idUser") UUID idUser, @RequestBody MesConges conge) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ ENVOYER LES DEMANDES  ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			PKPersonnel employee = personnelService.findPersonnelById(conge.getIdPersonnel());
			PKUser user = employee.getUser();

			List<LocalDate> days = Outils.getDatesBetweenConge(conge.getDateDebut(), conge.getDateReprise());
			List<LocalDate> daysConge = new ArrayList<LocalDate>();

			if (!(employee.getFNbJourConge() > 0)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: pas solde conge !"));
			}

			for (LocalDate date : days) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, employee.getId())) {

					return ResponseEntity.badRequest()
							.body(new MessageResponse("Error: la date : " + date + " déja exist dans la table TS !"));
				}

				String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
				log.info("DAY NAME :" + str);

				boolean jourferie = jourFerieReporsitory.existsByPays_idAndDateJoureFerie(employee.getPkPays().getId(),
						date);
				boolean jourTravail = horaireRepository.existsByJourIgnoreCaseAndSemaineTravails_Id(str,
						employee.getSemaineTravail().getId());

				if (!jourferie && jourTravail) {
					daysConge.add(date);
				}
			}
			if (!(employee.getFNbJourConge() > daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: solde conge "
						+ employee.getFNbJourConge() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKDemande demande = demandeSevice.findDemandeById(conge.getIdDemande());

			if (!Objects.equals(user, null)) {
				if (roleService.getNameRoleByUser(user).contains("RESPONSABLE")) {
					if (!Objects.equals(employee.getSuperieur(), null)) {
						log.info("Code super :" + employee.getSuperieur());
						log.info("demande super :" + demande.getCodeSup());
						demande.setCodeSup(employee.getSuperieur());
					} else {
						return ResponseEntity.badRequest()
								.body(new MessageResponse(" manque de supérieurs hiérarchiques "));
					}
				} else if (roleService.getNameRoleByUser(user).contains("SUPERVISEUR")) {

					if (!Objects.equals(employee.getService().getCodeResponsable(), null)) {
						demande.setCodeSup(employee.getService().getCodeResponsable());
					} else {
						return ResponseEntity.badRequest()
								.body(new MessageResponse(" manque de supérieurs hiérarchiques "));
					}
				} else {

					if (!Objects.equals(employee.getService().getCodeSuperviseur(), null)) {
						demande.setCodeSup(employee.getService().getCodeSuperviseur());
					} else {
						return ResponseEntity.badRequest()
								.body(new MessageResponse(" manque de supérieurs hiérarchiques "));
					}
				}
			} else {
				if (!Objects.equals(employee.getService().getCodeSuperviseur(), null)) {
					demande.setCodeSup(employee.getService().getCodeSuperviseur());
				} else {
					return ResponseEntity.badRequest()
							.body(new MessageResponse(" manque de supérieurs hiérarchiques "));
				}
			}
			for (PKConge mesConges : demande.getConges()) {
				mesConges.setStatus("envoyé");
				congeService.save(mesConges);
			}
			conge.setStatus("envoyé");
			demande.setStatus("envoyé pour validation");
			demandeSevice.save(demande);
			employee.setFNbJourConge(employee.getFNbJourConge() - daysConge.size());
			personnelService.save(employee);
			mailSenderService.sendEmail(personnelService.findPersonnelById(employee.getSuperieur()).getSEmail(),
					"Validation d'une demande de congé ",
					"Bonjour, cet mail vous a été envoyé automatiquement pour vous informer que vous avez une demande à valider.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");
			return new ResponseEntity<>(conge, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/annule/{idUser}")
	@PreAuthorize("hasRole('annule_demande_conge')")
	public ResponseEntity<?> annuleConge(@PathVariable("idUser") UUID idUser, @RequestBody MesConges conge) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ ANNULER LES DEMANDES  ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			PKDemande demande = demandeSevice.findDemandeById(conge.getIdDemande());

			for (PKConge mesConges : demande.getConges()) {
				mesConges.setStatus("annulé");
				congeService.save(mesConges);
			}
			conge.setStatus("annulé");

			demande.setStatus("annulé");
			demandeSevice.save(demande);

			return new ResponseEntity<>(conge, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/modifier/{idUser}")
	@PreAuthorize("hasRole('modifier_demande_conge')")
	public ResponseEntity<?> modifierConge(@PathVariable("idUser") UUID idUser, @RequestBody MesConges conge) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ MODIFIER DEMANDE  ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate startDate = LocalDate.parse(conge.getDateDebut().substring(0,10), formatter);
			LocalDate finDate = LocalDate.parse(conge.getDateReprise().substring(0,10), formatter);

			PKPersonnel employee = personnelService.findPersonnelById(conge.getIdPersonnel());

			if (congeReporsitory.existsByDateDebutAndDateRepriseAndDemande_Personnel_id(startDate, finDate,
					employee.getId())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la demande déja exist !!"));
			}

			List<LocalDate> days = Outils.getDatesBetweenConge(conge.getDateDebut().substring(0,10), conge.getDateReprise().substring(0,10));
			List<LocalDate> daysConge = new ArrayList<LocalDate>();

			if (!(employee.getFNbJourConge() > 0)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: pas solde conge !"));
			}

			for (LocalDate date : days) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, employee.getId())) {

					return ResponseEntity.badRequest()
							.body(new MessageResponse("Error: la date : " + date + " déja exist dans la table TS !"));
				}

				String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
				log.info("DAY NAME :" + str);

				boolean jourferie = jourFerieReporsitory.existsByPays_idAndDateJoureFerie(employee.getPkPays().getId(),
						date);
				boolean jourTravail = horaireRepository.existsByJourIgnoreCaseAndSemaineTravails_Id(str,
						employee.getSemaineTravail().getId());

				if (!jourferie && jourTravail) {
					daysConge.add(date);
				}
			}
			if(daysConge.size()<1){
				return ResponseEntity.badRequest().body(new MessageResponse("Vous avez choisie des jours fériés"));
			}
			if (!(employee.getFNbJourConge() > daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: solde conge "
						+ employee.getFNbJourConge() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKTypeConge typeConge = typeCongeService.findTypeCongeById(conge.getIdTypeConge());

			if (!(typeConge.getMax() >= daysConge.size())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: nombre max de conge "
						+ typeConge.getMax() + " inferieur nombre jous conge " + daysConge.size()));
			}
			PKDemande demande = demandeSevice.findDemandeById(conge.getIdDemande());

			for (PKConge cng : demande.getConges()) {

				cng.setDateDebut(startDate);
				cng.setDateReprise(finDate);
				cng.setTypeConge(typeConge);
				cng.setNombreJour(daysConge.size());

				congeService.save(cng);

			}
			return new ResponseEntity<>(conge, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

/*
	@GetMapping("/mesDemandes/{idUser}")
	@PreAuthorize("hasRole('find_mes_demandes')")
	public ResponseEntity<?> findMesDemande(@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "idTypeDmd", required = false, defaultValue = "") UUID idTypeDmd,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ DEMANDE CONTROLLER ] ~ [ FIND MES DEMANDES ]");

		try {
			PKUser user = userService.findOne(idUser);
			List<PKDemande> demandes = demandeSevice.findByPersonnelAndTypedemande(user.getPersonnel().getId(),
					idTypeDmd);

			if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DC")) {

				List<MesConges> conges = Outils.MapDemandeToConges(demandes);
				PagedListHolder<MesConges> listHolder = new PagedListHolder<MesConges>(conges);

				listHolder.setPageSize(size);
				listHolder.setPage(page);

				Page<MesConges> pages = new PageImpl<MesConges>(listHolder.getPageList(),
						PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), conges.size());
				return new ResponseEntity<>(pages, HttpStatus.OK);

			} else if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DDA")) {

				List<MesDocuments> documents = Outils.MapDemandeToDocument(demandes);
				PagedListHolder<MesDocuments> listHolder = new PagedListHolder<MesDocuments>(documents);

				listHolder.setPageSize(size);
				listHolder.setPage(page);

				Page<MesDocuments> pages = new PageImpl<MesDocuments>(listHolder.getPageList(),
						PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), documents.size());
				return new ResponseEntity<>(pages, HttpStatus.OK);
			}

			return ResponseEntity.badRequest().body(new MessageResponse(" aucun demande trouvé !! "));

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
*/
@GetMapping("/mesDemandes/{idUser}")
@PreAuthorize("hasRole('find_mes_demandes')")
public ResponseEntity<?> findMesDemande(@PathVariable("idUser") UUID idUser,
										@RequestParam( required = false, defaultValue = "") UUID idTypeDmd,
										@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
										@RequestParam (defaultValue = "true") boolean sort) {

	log.info("[ DEMANDE CONTROLLER ] ~ [ FIND MES DEMANDES ]");

	try {
		//ihistoriqueService.SaveHistorique(idUser,"cherecher les demandes");
		PKUser user = userService.findOne(idUser);
		//System.out.println(user);
		List<PKDemande> demandes = demandeSevice.findByPersonnelAndTypedemande(user.getPersonnel().getId(),
				idTypeDmd);
		if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DC")) {
			List<MesConges> conges = Outils.MapDemandeToConges(demandes);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			if(sort){
				Collections.sort(conges, (c1, c2) -> {
					LocalDate l1 = LocalDate.parse(c1.dateDemande,formatter);
					LocalDate l2 = LocalDate.parse(c2.dateDemande,formatter);
					return l1.compareTo(l2);
				} );
			}else{
				Collections.sort(conges, (c1, c2) -> {
					LocalDate l1 = LocalDate.parse(c1.dateDemande,formatter);
					LocalDate l2 = LocalDate.parse(c2.dateDemande,formatter);
					return -l1.compareTo(l2);
				} );
			}

			PagedListHolder<MesConges> listHolder = new PagedListHolder<MesConges>(conges);
			listHolder.setPageSize(size);
			listHolder.setPage(page);
			Page<MesConges> pages = new PageImpl<MesConges>(listHolder.getPageList(),
					PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), conges.size());
			return new ResponseEntity<>(pages, HttpStatus.OK);

		} else if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DDA")) {

			List<MesDocuments> documents = Outils.MapDemandeToDocument(demandes);
			PagedListHolder<MesDocuments> listHolder = new PagedListHolder<MesDocuments>(documents);

			listHolder.setPageSize(size);
			listHolder.setPage(page);

			Page<MesDocuments> pages = new PageImpl<MesDocuments>(listHolder.getPageList(),
					PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), documents.size());
			return new ResponseEntity<>(pages, HttpStatus.OK);
		}

		return ResponseEntity.badRequest().body(new MessageResponse(" aucun demande trouvé !! "));

	} catch (Exception ex) {
		ex.printStackTrace();
		log.error("ERROR : ", ex.getMessage());
		return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

	}
}

	@GetMapping("/demandes/{idUser}")
	@PreAuthorize("hasRole('find_demandes_affecte')")
	public ResponseEntity<?> findDemande(@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "idTypeDmd", required = false, defaultValue = "") UUID idTypeDmd,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ DEMANDE CONTROLLER ] ~ [ FIND DEMANDES AFFECTE ]");

		try {
			PKUser user = userService.findOne(idUser);
			List<PKDemande> demandes = demandeSevice.findByRespAndTypedemande(user.getPersonnel().getId(), idTypeDmd);

			if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DC")) {

				List<MesConges> conges = Outils.MapDemandeToConges(demandes);
				PagedListHolder<MesConges> listHolder = new PagedListHolder<MesConges>(conges);

				listHolder.setPageSize(size);
				listHolder.setPage(page);

				Page<MesConges> pages = new PageImpl<MesConges>(listHolder.getPageList(),
						PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), conges.size());
				return new ResponseEntity<>(pages, HttpStatus.OK);

			} else if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DDA")) {

				List<MesDocuments> documents = Outils.MapDemandeToDocument(demandes);
				PagedListHolder<MesDocuments> listHolder = new PagedListHolder<MesDocuments>(documents);

				listHolder.setPageSize(size);
				listHolder.setPage(page);

				Page<MesDocuments> pages = new PageImpl<MesDocuments>(listHolder.getPageList(),
						PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), documents.size());
				return new ResponseEntity<>(pages, HttpStatus.OK);
			}

			return ResponseEntity.badRequest().body(new MessageResponse(" aucun demande trouvé !! "));

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PutMapping("/decisionConge/{idUser}")
	@PreAuthorize("hasRole('decision_conge')")
	public ResponseEntity<?> decisionConge(@PathVariable("idUser") UUID idUser, @RequestBody Demande demande) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ DECISION DEMANDE CONGE ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			PKDemande d = demandeSevice.findDemandeById(demande.getIdDemande());
			String emailAdress=d.getPersonnel().getSEmail();
			//System.out.println(emailAdress);
			if (demande.getStatus().equals("validé")) {

				if (d.getStatus().equals("envoyé pour validation")) {

					d.setStatus("validé");
					d.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
					d.setDDateDecisionSup(LocalDate.now());
					d.setDecisionSup(userService.findOne(idUser).getUsername());
					for (PKConge conge : d.getConges()) {
						conge.setStatus("validé");
						congeService.save(conge);
					}
					demandeSevice.save(d);
					mailSenderService.sendEmail(emailAdress,
							"Concernant votre demande de congé ",
							"Bonjour, cet mail  vous a été envoyé automatiquement pour vous informer que votre demande en ligne sur PROCHECK a eu des réponses.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");
					return ResponseEntity.ok(new MessageResponse("La demande est validé"));

				}else if (d.getStatus().equals("refusé")) {

					d.setStatus("validé");
					d.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
					d.setDDateDecisionSup(LocalDate.now());
					d.setDecisionSup(userService.findOne(idUser).getUsername());
					for (PKConge conge : d.getConges()) {
						conge.setStatus("validé");
						congeService.save(conge);
						PKPersonnel personnel=personnelService.findPersonnelById(d.getPersonnel().getId());
						personnel.setFNbJourConge(personnel.getFNbJourConge() - conge.getNombreJour() );
						personnelService.save(personnel);
					}
					demandeSevice.save(d);
					mailSenderService.sendEmail(emailAdress,
							"Concernant votre demande de congé ",
							"Bonjour, cet mail  vous a été envoyé automatiquement pour vous informer que votre demande en ligne sur PROCHECK a eu des réponses.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");
					return ResponseEntity.ok(new MessageResponse("La demande est validé"));

				} else if (d.getStatus().equals("demande modifier")) {

					d.setStatus("enregistré");
					d.setCodeSup(null);
					
					for (PKConge conge : d.getConges()) {
						conge.setStatus("crée");
						congeService.save(conge);
						PKPersonnel personnel=personnelService.findPersonnelById(d.getPersonnel().getId());
						personnel.setFNbJourConge(personnel.getFNbJourConge() + conge.getNombreJour() );
						personnelService.save(personnel);
					}
					demandeSevice.save(d);
					mailSenderService.sendEmail(emailAdress,
							"Concernant votre demande de congé ",
							"Bonjour, cet mail  vous a été envoyé automatiquement pour vous informer que votre demande en ligne sur PROCHECK a eu des réponses.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");
					return ResponseEntity.ok(new MessageResponse("La demande est enregistrée"));
				}
			}
			else if (demande.getStatus().equals("refusé")) {

				if (d.getStatus().equals("envoyé pour validation")) {

					d.setStatus("refusé");
					d.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
					d.setDDateDecisionSup(LocalDate.now());
					d.setDecisionSup(userService.findOne(idUser).getUsername());
					for (PKConge conge : d.getConges()) {
						conge.setStatus("refusé");
						congeService.save(conge);
						PKPersonnel personnel=personnelService.findPersonnelById(d.getPersonnel().getId());
						personnel.setFNbJourConge(personnel.getFNbJourConge() + conge.getNombreJour() );
						personnelService.save(personnel);
					}
					demandeSevice.save(d);
					mailSenderService.sendEmail(emailAdress,
							"Concernant votre demande de congé ",
							"Bonjour, cet mail  vous a été envoyé automatiquement pour vous informer que votre demande en ligne sur PROCHECK a eu des réponses.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");

					return ResponseEntity.ok(new MessageResponse("La demande est refusé"));

				}else if (d.getStatus().equals("validé")) {

					d.setStatus("refusé");
					d.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
					d.setDDateDecisionSup(LocalDate.now());
					d.setDecisionSup(userService.findOne(idUser).getUsername());
					for (PKConge conge : d.getConges()) {
						conge.setStatus("refusé");
						congeService.save(conge);
						PKPersonnel personnel=personnelService.findPersonnelById(d.getPersonnel().getId());
						personnel.setFNbJourConge(personnel.getFNbJourConge() + conge.getNombreJour() );
					}
					demandeSevice.save(d);
					mailSenderService.sendEmail(emailAdress,
							"Concernant votre demande de congé ",
							"Bonjour, cet mail  vous a été envoyé automatiquement pour vous informer que votre demande en ligne sur PROCHECK a eu des réponses.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");
					return ResponseEntity.ok(new MessageResponse("La demande est refusé"));

				} else if (d.getStatus().equals("demande modifier")) {

					d.setStatus("demande refusé");
					d.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
					d.setDDateDecisionSup(LocalDate.now());
					d.setDecisionSup(userService.findOne(idUser).getUsername());
					for (PKConge conge : d.getConges()) {
						conge.setStatus("validé");
						congeService.save(conge);
					}
					demandeSevice.save(d);
					mailSenderService.sendEmail(emailAdress,
							"Concernant votre demande de congé ",
							"Bonjour, cet mail  vous a été envoyé automatiquement pour vous informer que votre demande en ligne sur PROCHECK a eu des réponses.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");

					return ResponseEntity.ok(new MessageResponse("La demande est refusé"));

				}

			}
			else if (demande.getStatus().equals("accepté")) {

				if (d.getStatus().equals("demande annulation")) {

					d.setStatus("annulé");
					d.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
					d.setDDateDecisionSup(LocalDate.now());
					d.setDecisionSup(userService.findOne(idUser).getUsername());
					for (PKConge conge : d.getConges()) {
						conge.setStatus("annulé");
						PKPersonnel personnel=personnelService.findPersonnelById(d.getPersonnel().getId());
						personnel.setFNbJourConge(personnel.getFNbJourConge() + conge.getNombreJour() );
						personnelService.save(personnel);
						congeService.save(conge);
					}
					demandeSevice.save(d);
					mailSenderService.sendEmail(emailAdress,
							"Concernant votre demande d' annulation de congé ",
							"Bonjour, cet mail  vous a été envoyé automatiquement pour vous informer que votre demande en ligne sur PROCHECK a eu des réponses.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");
					return ResponseEntity.ok(new MessageResponse("La demande d' annulation est accéptée."));

				}
			}
			else if (demande.getStatus().equals("rejeté")) {

				if (d.getStatus().equals("demande annulation")) {

					d.setStatus("validé après demande annulation");
					d.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
					d.setDDateDecisionSup(LocalDate.now());
					d.setDecisionSup(userService.findOne(idUser).getUsername());
					for (PKConge conge : d.getConges()) {
						conge.setStatus("validé après demande annulation");
						congeService.save(conge);
					}
					demandeSevice.save(d);
					mailSenderService.sendEmail(emailAdress,
							"Concernant votre demande d' annulation de congé ",
							"Bonjour, cet mail  vous a été envoyé automatiquement pour vous informer que votre demande en ligne sur PROCHECK a eu des réponses.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");
					return ResponseEntity.ok(new MessageResponse("La demande d' annulation est rejetée."));

				}
			}

			return ResponseEntity.badRequest().body(new MessageResponse(" la decision n'est pas enregistrée !! "));

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	@GetMapping("/demandes/byFilter/{idUser}")
	@PreAuthorize("hasRole('find_demand_by_filter')")
	public ResponseEntity<?> findDemandsByFilter(
			@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "matricule", required = false, defaultValue = "") String matricule,
			@RequestParam(value = "nom", required = false, defaultValue = "") String nom,
			@RequestParam(value = "prenom", required = false, defaultValue = "") String prenom,
			@RequestParam(value = "status", required = false, defaultValue = "") String status,
			@RequestParam(value = "date", required = false, defaultValue = "") String date,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ DEMANDES CONTROLLER ] ~ [ GET DEMANDES BY UTILISATEUR MATRICULE OR NOM OR PRENOM NON AFFECTE ]");

		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			
			if (DateValidator.isValid(date)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la date demande invalide !"));
			}
			
			
			PKUser user=userService.findOne(idUser);
			LocalDate dateD=null;
			if(!date.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			dateD = LocalDate.parse(date, formatter);
			}
			
			DemandeFilter demande=new DemandeFilter(user.getPersonnel().getId(),matricule,nom,prenom,status,dateD);
			
			List<PKDemande> demandes=demandeSevice.getDemandeByFilter(demande);

			return new ResponseEntity<>(demandes, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}
	
	@PutMapping("/demandeModifier/{idUser}")
	@PreAuthorize("hasRole('demande_modifier_conge')")
	public ResponseEntity<?> demandeModifierConge(@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "idDemande", required = false, defaultValue = "") UUID idDemande) {
		log.info("[ DEMANDE CONTROLLER ] ~ [ DEMANDE MODIFIER CONGE ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			PKDemande d = demandeSevice.findDemandeById(idDemande);

			if (d.getStatus().equals("validé")) {
					d.setStatus("demande modifier");
					demandeSevice.save(d);
			}
			
			return ResponseEntity.badRequest().body(new MessageResponse(" la decision sont bien enregistré !! "));

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	//mon code

	@GetMapping("/typeDemande/{codeTypeDemande}")
	@PreAuthorize("hasRole('get_type_demande')")
	// privilège à ajouter
	public ResponseEntity<?> findTypeDemandeByCodeTypeDemande(@PathVariable(name = "codeTypeDemande") String codeTypeDemande){
		try {
			PKTypeDemande typeDemande = typeDemandeService.findTypeDemandeByCode(codeTypeDemande);
			return new ResponseEntity(typeDemande, HttpStatus.OK);
		}catch(Exception ex){

			return ResponseEntity.badRequest().body("Error : "+ex.getMessage());
		}
	}

	//get Mesconges from idDemande
	@GetMapping("/{idDemande}")
	@PreAuthorize("hasRole('find_demande_conge')")
	public ResponseEntity<?> getDemande(@PathVariable(value="idDemande") UUID idDemande ,
										@RequestParam(defaultValue = "0") int page,
										@RequestParam(defaultValue = "10") int size) {

		try{
			PKDemande demande= demandeSevice.findDemandeById(idDemande);
			List<PKDemande> demandes = new ArrayList<>();
			demandes.add(demande);
			List<MesConges> conges = Outils.MapDemandeToConges(demandes);
			PagedListHolder<MesConges> listHolder = new PagedListHolder<MesConges>(conges);

			listHolder.setPageSize(size);
			listHolder.setPage(page);

			Page<MesConges> pages = new PageImpl<MesConges>(listHolder.getPageList(),
					PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), conges.size());
			return new ResponseEntity<>(pages, HttpStatus.OK);

		}catch(Exception e){
			return new ResponseEntity<>("Demande introuvable",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/demandes/byCodeSup/{codeSup}")
	@PreAuthorize("hasRole('demande_conge_by_superieur')")
	public ResponseEntity<?> getDemandeByCodeSup(@PathVariable(value="codeSup") UUID codeSup ,
												 @RequestParam(value="idTypeDmd") UUID idTypeDmd,
										@RequestParam(defaultValue = "0") int page,
										@RequestParam(defaultValue = "10") int size,
										@RequestParam (defaultValue = "true") boolean sort) {

		try {
			List<PKDemande> demandes = demandeSevice.findByCodeSup(userService.findOne(codeSup).getPersonnel().getId());
			if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DC")) {
				List<MesConges> conges = Outils.MapDemandeToConges(demandes);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				if (sort) {
					Collections.sort(conges, (c1, c2) -> {
						LocalDate l1 = LocalDate.parse(c1.dateDemande, formatter);
						LocalDate l2 = LocalDate.parse(c2.dateDemande, formatter);
						return l1.compareTo(l2);
					});
				} else {
					Collections.sort(conges, (c1, c2) -> {
						LocalDate l1 = LocalDate.parse(c1.dateDemande, formatter);
						LocalDate l2 = LocalDate.parse(c2.dateDemande, formatter);
						return -l1.compareTo(l2);
					});
				}

				PagedListHolder<MesConges> listHolder = new PagedListHolder<MesConges>(conges);

				listHolder.setPageSize(size);
				listHolder.setPage(page);

				Page<MesConges> pages = new PageImpl<MesConges>(listHolder.getPageList(),
						PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), conges.size());
				return new ResponseEntity<>(pages, HttpStatus.OK);


			} else if (typeDemandeService.findTypeDemandeById(idTypeDmd).getCodeTypeDemande().equals("DDA")) {

				List<MesDocuments> documents = Outils.MapDemandeToDocument(demandes);
				PagedListHolder<MesDocuments> listHolder = new PagedListHolder<MesDocuments>(documents);

				listHolder.setPageSize(size);
				listHolder.setPage(page);

				Page<MesDocuments> pages = new PageImpl<MesDocuments>(listHolder.getPageList(),
						PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), documents.size());
				return new ResponseEntity<>(pages, HttpStatus.OK);
			}

			return ResponseEntity.badRequest().body(new MessageResponse(" aucun demande trouvé !! "));
		}catch(Exception e){
			return new ResponseEntity<>("Code superieur introuvable",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/addBySuperieur/{idUser}/{idPersonnel}")
	@PreAuthorize("hasRole('demande_conge_by_superieur')")
	public ResponseEntity<?> addDemandeBySuperieur(@PathVariable("idUser") UUID idUser,
												   @PathVariable("idPersonnel") UUID idPersonnel,
												   @Valid @RequestBody Conge conge){
		log.info("[ DEMANDE CONTROLLER ] ~ [ CREATE DEMANDE CONGE BY SUPERIEUR ]");
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate startDate = LocalDate.parse(conge.getDateDebut().substring(0,10), formatter).plusDays(1);
			LocalDate finDate = LocalDate.parse(conge.getDateReprise().substring(0,10), formatter).plusDays(1);
			//PKUser user = userService.findOne(idUser);
			//PKPersonnel employee = user.getPersonnel();
			PKPersonnel employee= personnelService.findPersonnelById(idPersonnel);
			//ihistoriqueService.SaveHistorique(idUser," Ajouter demande congé "+employee.getSNom());
			if (congeReporsitory.existsByDateDebutAndDateRepriseAndDemande_Personnel_id(startDate, finDate,
					employee.getId())) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la demande déja exist !!"));
			}

			List<LocalDate> days = Outils.getDatesBetweenConge(conge.getDateDebut().substring(0,10), conge.getDateReprise().substring(0,10));
			List<LocalDate> daysConge = new ArrayList<LocalDate>();
			if (!(employee.getFNbJourConge() > 0)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: pas solde conge !"));
			}
			for (LocalDate date : days) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, employee.getId())) {

					return ResponseEntity.badRequest().body(
							new MessageResponse("Error: la date : " + date + " déja exist dans la table TS !"));
				}

				String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
				log.info("DAY NAME :" + str);
				boolean jourferie = jourFerieReporsitory
						.existsByPays_idAndDateJoureFerie(employee.getPkPays().getId(), date);
				boolean jourTravail = horaireRepository.existsByJourIgnoreCaseAndSemaineTravails_Id(str,
						employee.getSemaineTravail().getId());
				if (!jourferie && jourTravail) {
					daysConge.add(date);
				}
			}
			if(daysConge.size()<1){
				return ResponseEntity.badRequest().body(new MessageResponse("Vous avez choisie des jours fériés"));
			}
			if (!(employee.getFNbJourConge() > daysConge.size())) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: solde conge "
						+ employee.getFNbJourConge() + " inferieur nombre jous conge " + daysConge.size()));
			}

			PKTypeConge typeConge = typeCongeService.findTypeCongeById(conge.getTypeConge());
			if (!(typeConge.getMax() >= daysConge.size())) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: nombre max de conge "
						+ typeConge.getMax() + " inferieur nombre jous conge " + daysConge.size()));
			}
			PKDemande demande = new PKDemande();
			demande.setDDateCreation(LocalDate.now());
			demande.setCodeDemandeur(userService.findOne(idUser).getUsername());

			demande.setPersonnel(employee);
			demande.setStatus("envoyé pour validation");
			demande.setTypedemande(typeDemandeService.findTypeDemandeById(conge.typeDemande));
			demande.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
			PKConge dconge = new PKConge();
			dconge.setName(conge.getName());
			dconge.setDescription(conge.getDescription());
			dconge.setDateDebut(startDate);
			dconge.setDateReprise(finDate);
			dconge.setNombreJour(daysConge.size());
			dconge.setTypeConge(typeConge);
			dconge.setStatus("envoyé");
			dconge.setDemande(demande);
			congeService.save(dconge);
			mailSenderService.sendEmail(personnelService.findPersonnelById(employee.getSuperieur()).getSEmail(),
					"Validation d'une demande de congé ",
					"Bonjour, cet mail vous a été envoyé automatiquement pour vous informer que vous avez une demande à valider.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");


			return new ResponseEntity<>(new MessageResponse("la demande de congé est bien enregistrée !"),
					HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/demandeAnnulation/{idUser}")
// privilege
	public ResponseEntity<?> demandeAnnulation(@PathVariable(name = "idUser") UUID idUser,
											   @RequestBody MesConges conge
											   ){
		log.info("[ DEMANDE CONTROLLER ] ~ [ DEMANDE ANNULATION APRES VALIDATION DES DEMANDES  ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			PKDemande demande = demandeSevice.findDemandeById(conge.getIdDemande());
			List<PKConge> conge_demande= (List)(demande.getConges());
			// Calculate the difference in days
			long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), conge_demande.get(0).getDateDebut());

			System.out.println("Difference in days: " + daysDifference);
			if(daysDifference>1){
				demande.setStatus("demande annulation");
				demandeSevice.save(demande);
				mailSenderService.sendEmail(personnelService.findPersonnelById(demande.getPersonnel().getSuperieur()).getSEmail(),
						"Demande d'annulation d'un congé ",
						"Bonjour, cet mail vous a été envoyé automatiquement pour vous informer que vous avez une demande d'annulation d'un congé à confirmer.\nPrière de ne pas répondre à cet email automatique, votre réponse ne sera pas lue.\n\nCordialement. ");

				return new ResponseEntity<>(demande, HttpStatus.OK);
			}else{
				return ResponseEntity.badRequest().body(new MessageResponse("La demande d'annulation doit être faite 2 jours avant la date de debut du congé"));
			}



			//dateDebut.
			/*for (PKConge mesConges : demande.getConges()) {
				mesConges.setStatus("annulé");
				congeService.save(mesConges);
			}*/
			//conge.setStatus("demande annulation");

			/*demande.setStatus("demande annulation");
			demandeSevice.save(demande);*/

			//return new ResponseEntity<>("Demande d'annulation est faite", HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}

