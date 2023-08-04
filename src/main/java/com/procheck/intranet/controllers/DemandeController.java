package com.procheck.intranet.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.validation.Valid;

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

			demande.setStatus("en cours");
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
			demande.setStatus("en cours");
			demandeSevice.save(demande);
			employee.setFNbJourConge(employee.getFNbJourConge() - daysConge.size());
			personnelService.save(employee);

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
		log.info("[ DEMANDE CONTROLLER ] ~ [ ENVOYER LES DEMANDES  ]");
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

			if (demande.getStatus().equals("validé")) {

				if (d.getStatus().equals("en cours")) {

					d.setStatus("validé");
					d.setCodeSup(userService.findOne(idUser).getPersonnel().getId());
					d.setDDateDecisionSup(LocalDate.now());
					d.setDecisionSup(userService.findOne(idUser).getUsername());
					for (PKConge conge : d.getConges()) {
						conge.setStatus("validé");
						congeService.save(conge);
					}
					demandeSevice.save(d);

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
				}
			}
			if (demande.getStatus().equals("refusé")) {

				if (d.getStatus().equals("en cours")) {

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
				}
			}

			return ResponseEntity.badRequest().body(new MessageResponse(" la decision sont bien enregistré !! "));

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
}
