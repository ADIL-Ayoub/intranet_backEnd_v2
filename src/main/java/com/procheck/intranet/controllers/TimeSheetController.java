package com.procheck.intranet.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.outils.DateValidator;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.ConfermationTs;
import com.procheck.intranet.payload.request.DemandeMTSFilter;
import com.procheck.intranet.payload.request.DemandeModificationTs;
import com.procheck.intranet.payload.request.DemiHoraire;
import com.procheck.intranet.payload.request.ModificationTs;
import com.procheck.intranet.payload.request.PersonnelFilterByService;
import com.procheck.intranet.payload.request.Timesheet;
import com.procheck.intranet.payload.request.TimsheetByProjet;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.ModificationTSRepository;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.repository.TimesheetReporsitory;
import com.procheck.intranet.repository.UserRepository;
import com.procheck.intranet.services.IHoraireService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.IProjetTimesheet;
import com.procheck.intranet.services.ISemaineTravailService;
import com.procheck.intranet.services.IServiceService;
import com.procheck.intranet.services.ITimesheetService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/timesheet")
public class TimeSheetController {

	@Autowired
	IHoraireService horaireService;

	@Autowired
	ISemaineTravailService semaineTravailService;

	@Autowired
	PersonnelReporsitory personnelReporsitory;

	@Autowired
	ServiceReporsitory serviceReporsitory;

	@Autowired
	IServiceService serviceService;

	@Autowired
	IPersonnelService personnelService;

	@Autowired
	ITimesheetService timesheetService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	IProjetTimesheet timesheetProjet;

	@Autowired
	ModificationTSRepository modificationTsRepository;

	@Autowired
	TimesheetReporsitory timesheetRepository;

	@GetMapping("/TimesheetByUser/{idUser}")
	@PreAuthorize("hasRole('user_generation_ts')")
	public ResponseEntity<?> generationHoraireByEmployee(@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "periode", required = false, defaultValue = "") String periode,
			@RequestParam(value = "DateD", required = false, defaultValue = "") String dateD,
			@RequestParam(value = "DateF", required = false, defaultValue = "") String dateF) {

		log.info("[ TIMESHEET CONTROLLER ] ~ [ GENERATION TS BY USER ]");
		try {
			if (periode.equals("") || dateD.equals("") || dateF.equals("")) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: paramètres incorects  !"));
			}
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			List<Timesheet> timesheets = timesheetService.createTimeSheetUser(idUser, dateD, dateF, periode);
			List<Timesheet> times = new ArrayList<Timesheet>();

			for (Timesheet timesheet : timesheets) {

				Timesheet last = times.stream().filter(j -> j.getIdPersonnel().equals(timesheet.getIdPersonnel()))
						.findFirst().orElse(null);
				if (last != null) {
					int index = times.indexOf(last);

					if (!Objects.equals(last.getHoraires(), null)) {
						Set<DemiHoraire> horaires = new HashSet<DemiHoraire>(last.getHoraires());
						horaires.addAll(timesheet.getHoraires());
						last.setHoraires(new ArrayList<DemiHoraire>(horaires));
						last.getHoraires().sort(Comparator.comparing(DemiHoraire::getDateTimesheet));
					}

					times.set(index, last);

				} else {

					times.add(timesheet);
				}
			}
			return new ResponseEntity<>(times, HttpStatus.OK);
		} catch (Exception e) {
			log.error("ERROR : ", e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>("ERROR : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PutMapping("/confermationTimesheetByEmp/{idUser}")
	@PreAuthorize("hasRole('confermation_ts_emp')")
	public ResponseEntity<?> confermationTSParUser(@PathVariable("idUser") UUID idUser,
			@RequestBody Timesheet timesheet) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ CONFIRMATION TS EMPLOYEE ]");

		try {

			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			
			Timesheet time= timesheetService.conferamtionTsEmp(idUser, timesheet);
			
			return new ResponseEntity<>(time, HttpStatus.OK);
			
		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@PutMapping("/enregistrementTimesheetByEmp/{idUser}")
	@PreAuthorize("hasRole('enregistrement_ts_emp')")
	public ResponseEntity<?> enregistrementTSParUser(@PathVariable("idUser") UUID idUser,
			@RequestBody Timesheet timesheet) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ ENREGISTREMENT TS EMPLOYEE ]");

		try {

			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			
			Timesheet time= timesheetService.enregistrementTsEmp(idUser, timesheet);
			
			return new ResponseEntity<>(time, HttpStatus.OK);
			
		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/TimesheetByService/{idUser}")
	@PreAuthorize("hasRole('generation_ts')")
	public ResponseEntity<?> generationHoraireByServices(@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "services", required = false, defaultValue = "") List<UUID> services,
			@RequestParam(value = "cin", required = false, defaultValue = "") String cin,
			@RequestParam(value = "nom", required = false, defaultValue = "") String nom,
			@RequestParam(value = "prenom", required = false, defaultValue = "") String prenom,
			@RequestParam(value = "matrucule", required = false, defaultValue = "") String matrucule,
			@RequestParam(value = "post", required = false, defaultValue = "") String post,
			@RequestParam(value = "periode", required = false, defaultValue = "") String periode,
			@RequestParam(value = "DateD", required = false, defaultValue = "") String dateD,
			@RequestParam(value = "DateF", required = false, defaultValue = "") String dateF,
			@RequestParam(value = "projet", required = false) Boolean projet,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ TIMESHEET CONTROLLER ] ~ [ GENERATION TS BY SERVECES ]");
		try {
			if (periode.equals("") || dateD.equals("") || dateF.equals("")) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: paramètres incorects  !"));
			}
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}

			for (UUID id : services) {
				if (!serviceReporsitory.existsById(id)) {
					return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id  !"));
				}
			}

			PersonnelFilterByService filter = new PersonnelFilterByService(services, cin.toUpperCase(),
					nom.toUpperCase(), prenom.toUpperCase(), matrucule.toUpperCase(), post.toUpperCase(), projet);

			List<Timesheet> timesheets = timesheetService.createTimeSheet(filter, dateD, dateF, periode, idUser);
			List<Timesheet> times = new ArrayList<Timesheet>();

			for (Timesheet timesheet : timesheets) {

				Timesheet last = times.stream().filter(j -> j.getIdPersonnel().equals(timesheet.getIdPersonnel()))
						.findFirst().orElse(null);
				if (last != null) {
					int index = times.indexOf(last);

					if (!Objects.equals(last.getHoraires(), null)) {
						Set<DemiHoraire> horaires = new HashSet<DemiHoraire>(last.getHoraires());
						horaires.addAll(timesheet.getHoraires());
						last.setHoraires(new ArrayList<DemiHoraire>(horaires));
						// Collections.sort(last.getDemihoraires(),(o1,o2) ->
						// o1.getDateTimesheet().compareTo(o2.getDateTimesheet()));
						last.getHoraires().sort(Comparator.comparing(DemiHoraire::getDateTimesheet));
					}

					times.set(index, last);

				} else {

					times.add(timesheet);
				}
			}

			PagedListHolder<Timesheet> listHolder = new PagedListHolder<Timesheet>(times);

			listHolder.setPageSize(size);
			listHolder.setPage(page);

			Page<Timesheet> pages = new PageImpl<Timesheet>(listHolder.getPageList(),
					PageRequest.of(listHolder.getPage(), listHolder.getPageSize()), times.size());

			return new ResponseEntity<>(pages, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/TimesheetByPersonne/{id}")
	@PreAuthorize("hasRole('find_timesheet_personnel')")
	public ResponseEntity<?> generateTSByPersoneel(@PathVariable("id") UUID id,
			@RequestParam(value = "dateD", required = false, defaultValue = "") String dateD,
			@RequestParam(value = "dateF", required = false, defaultValue = "") String dateF) {
		try {
			if (!DateValidator.isValid(dateD) || !DateValidator.isValid(dateF)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: les dates timesheet invalide !"));
			}

			Timesheet timesheet = timesheetService.findTimesheetPersonnel(id, dateD, dateF);

			return new ResponseEntity<>(timesheet, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/ValidationTimesheetByIds/{idUser}")
	@PreAuthorize("hasRole('validation_ts')")
	public ResponseEntity<?> validationTs(@PathVariable("idUser") UUID idUser,
			@RequestBody List<Timesheet> timesheets) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ VALIDATION TS ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			List<Timesheet> times = timesheetService.validationTsByIds(idUser, timesheets);

			return new ResponseEntity<>(times, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/EnregistrementTimesheetByIds/{idUser}")
	@PreAuthorize("hasRole('enregistrement_ts')")
	public ResponseEntity<?> enregistrementTs(@PathVariable("idUser") UUID idUser,
			@RequestBody List<Timesheet> timesheets) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ ENREGISTREMENT TS ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));

			}
			List<Timesheet> times = timesheetService.enregistrementTsByIds(idUser, timesheets);

			return new ResponseEntity<>(times, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			ex.printStackTrace();
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/addTsProjet/{idUser}")
	@PreAuthorize("hasRole('add_ts_projet')")
	public ResponseEntity<?> add(@PathVariable("idUser") UUID idUser,
			@Valid @RequestBody List<TimsheetByProjet> tsProjets) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ AJOUTER TS PROJETS ]");

		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));
			}
			return new ResponseEntity<>(timesheetProjet.save(idUser, tsProjets), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/modTsProjet/{id}")
	@PreAuthorize("hasRole('update_ts_projet')")
	public ResponseEntity<?> modifier(@PathVariable("id") UUID id, @RequestBody TimsheetByProjet tsProjet) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ UPDATE TS PROJET BY ID ]");
		try {
			if (!timesheetProjet.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found ts projet with id !"));

			}

			timesheetProjet.update(id, tsProjet);

			return new ResponseEntity<>(new MessageResponse("timesheet registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/modTs/{id}")
	@PreAuthorize("hasRole('update_ts_projet')")
	public ResponseEntity<?> modifierTs(@PathVariable("id") UUID id, @RequestBody TimsheetByProjet tsProjet) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ UPDATE TS PROJET BY ID ]");
		try {
			if (!timesheetProjet.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found ts projet with id !"));

			}

			timesheetProjet.update(id, tsProjet);

			return new ResponseEntity<>(new MessageResponse("timesheet registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delTsProjet/{id}")
	@PreAuthorize("hasRole('delete_ts_projet')")
	public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ DELETE TS PROJET ]");
		try {

			if (!timesheetProjet.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found ts projet with id !"));

			}
			timesheetProjet.delete(id);

			return ResponseEntity.ok(new MessageResponse("timesheet delete successfully!"));

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/demandeModificationTs/{idUser}")
	@PreAuthorize("hasRole('add_demande_ts')")
	public ResponseEntity<?> addDemandeModificationTS(@PathVariable("idUser") UUID idUser,
			@Valid @RequestBody DemandeModificationTs demande) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ AJOUTER DEMANDE MODIFICATION TS ]");

		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));
			}
			if (!demande.isModifier()) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: TimeSheet non modifier !"));
			}
			if (modificationTsRepository.existsByStatusAndTimesheet_id("en cours", demande.getIdTimesheet())) {

				return ResponseEntity.ok(new MessageResponse("la demande déja envoyée !"));

			}
			timesheetService.createDemande(idUser, demande);
			return ResponseEntity.ok(new MessageResponse("la demande bien envoyée !"));

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/confermationDemandeTs/{idUser}")
	@PreAuthorize("hasRole('update_demande_ts')")
	public ResponseEntity<?> confermationDemandeTs(@PathVariable("idUser") UUID idUser,
			@RequestBody List<ConfermationTs> confermations) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ CONFERMATION MODIFICATION TS BY ID ]");
		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));
			}

			timesheetService.confermationDemandeTS(idUser, confermations);

			return new ResponseEntity<>(new MessageResponse("Opération registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/MesDemandesModificationTs/{idUser}")
	@PreAuthorize("hasRole('find_demande_ts')")
	public ResponseEntity<?> mesDemandsMTS(@PathVariable("idUser") UUID idUser,
			@RequestParam(value = "status", required = false, defaultValue = "") String status,
			@RequestParam(value = "date", required = false, defaultValue = "") String date,
			@RequestParam(value = "demandeur", required = false, defaultValue = "") String demandeur,
			@RequestParam(value = "type", required = false, defaultValue = "") String type,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		log.info("[ TIMESHEET CONTROLLER ] ~ [ AJOUTER TS PROJETS ]");

		try {
			if (!userRepository.existsById(idUser)) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: user is not exist !"));
			}
			if (!DateValidator.isValid(date) && !date.isEmpty()) {

				return ResponseEntity.badRequest().body(new MessageResponse("Error: la date demande invalide !"));
			}
			log.info("date validation :" + DateValidator.isValid(date));

			LocalDate localDateD = DateValidator.StringToDate(date);
			DemandeMTSFilter demande = new DemandeMTSFilter(idUser, status, localDateD, type);
			return new ResponseEntity<>(timesheetService.findDemandeMTSByFilter(demande, size, page), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
