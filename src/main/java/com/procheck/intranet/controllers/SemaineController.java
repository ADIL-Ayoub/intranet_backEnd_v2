package com.procheck.intranet.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKTimesheet;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.AffectationSemaine;
import com.procheck.intranet.payload.request.DemiHoraire;
import com.procheck.intranet.payload.request.ParamRequest;
import com.procheck.intranet.payload.request.PersonnelFilterByService;
import com.procheck.intranet.payload.request.Timesheet;
import com.procheck.intranet.payload.request.UserEmailRoles;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.repository.SemaineReporsitory;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.services.IHoraireService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.ISemaineTravailService;
import com.procheck.intranet.services.IServiceService;
import com.procheck.intranet.services.ITimesheetService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/semaine")
public class SemaineController {

	@Autowired
	SemaineReporsitory semaineReporsitory;

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

	@PutMapping("/idServicesSemaine")
	@PreAuthorize("hasRole('semaine_employees')")
	public ResponseEntity<?> affectationSemaineEmployee(@RequestBody AffectationSemaine modele) {
		log.info("[ USER CONTROLLER ] ~ [ UPDATE EMAIL ROLES USER BY ID ]");
		try {
			
		if (!semaineReporsitory.existsById(modele.idSemaine)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found semaine with id !"));
		}
		for (UUID idService : modele.idServies) {
			if (!serviceReporsitory.existsById(idService)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id  !"));
			}
		}
		
			semaineTravailService.affecationEmployees(modele);

			return new ResponseEntity<>(new MessageResponse("semaine registered successfully!"), HttpStatus.OK);

		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	
//	@GetMapping("/SemaineByEmployee/{id}/{dateD}/{dateF}")
//	@PreAuthorize("hasRole('find_semaine_jours')")
//	public ResponseEntity<?> findHoraireSemaine(@PathVariable("id") UUID id, @PathVariable("dateD") String dateD,
//			@PathVariable("dateF") String dateF) {
//
//		log.info("[ SEMAINE CONTROLLER ] ~ [ FIND HORAIRE BY ID PERSONNEL AND DATE ]");
//
//		try {
//			if (!personnelReporsitory.existsById(id)) {
//				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id  !"));
//			}
//			List<Timesheet> timesheets = new ArrayList<Timesheet>();
//			for (LocalDate date : Outils.getDatesBetween(dateD, dateF)) {
//				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//				String strDate = dateFormat.format(date);
//				if (timesheetService.existsByDDateTimesheetAndPersonnel_id(date, id)) {
//
//					PKTimesheet timesheet = timesheetService.findByDDateTimesheetAndPersonnel(date, id);
//					Timesheet time = new Timesheet();
//					time.setIdPersonnel(id);
////					Horaire h = new Horaire();
////					h.setDateTimesheet(strDate);
////					h.setParty1He(timesheet.getSParty1He());
////					h.setParty1Hs(timesheet.getSParty1Hs());
////					h.setParty2He(timesheet.getSParty2He());
////					h.setParty2Hs(timesheet.getSParty2Hs());
////					h.setAbsent(timesheet.isNAbsent());
////					h.setAbsenceMotif(timesheet.getSAbsenceMotif());
////					time.getHoraires().add(h);
////					timesheets.add(time);
//				}
//				else {
//					
//				}
//			}
//			return new ResponseEntity<>(timesheets, HttpStatus.OK);
//
//		} catch (Exception ex) {
//			log.error("ERROR : ", ex.getMessage());
//			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//	}
//
//	@GetMapping("/JournalierByEmployee/{id}/{date}")
//	@PreAuthorize("hasRole('find_semaine_jour')")
//	public ResponseEntity<?> findHoraireJournalier(@PathVariable("id") UUID id, @PathVariable("date") String date) {
//
//		log.info("[ SEMAINE CONTROLLER ] ~ [ FIND HORAIRE BY ID PERSONNEL AND DATE ]");
//
//		try {
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//			LocalDate d = LocalDate.parse(date, formatter);
//			Format f = new SimpleDateFormat("EEEE");  
//			String str = f.format(d);  
//			
//			log.info("DAY NAME :"+str);
//			
//			return new ResponseEntity<>(semaineTravailService.findByPersonnels_idAndHoraireByDate(id, str), HttpStatus.OK);
//			
////			if (!personnelReporsitory.existsById(id)) {
////				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found personnel with id  !"));
////			}
////			if (timesheetService.existsByDDateTimesheetAndPersonnel_id(d, id)) {
////
////				return new ResponseEntity<>(timesheetService.findByDDateTimesheetAndPersonnel(d, id), HttpStatus.OK);
////			}
////			return new ResponseEntity<>(semaineTravailService.findByIdPersonnel(id), HttpStatus.OK);
//
//		} catch (Exception ex) {
//			log.error("ERROR : ", ex.getMessage());
//			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//	}

//	@GetMapping("/TimesheetByService")
//	@PreAuthorize("hasRole('find_semaine_jours')")
//	public ResponseEntity<?> generationHoraireByServices(
//			@RequestParam(value = "services", required = false, defaultValue = "") List<UUID> services,
//			@RequestParam(value = "periode", required = false, defaultValue = "") String periode,
//			@RequestParam(value = "type", required = false, defaultValue = "") String type,
//			@RequestParam(value = "DateD", required = false, defaultValue = "") String dateD,
//			@RequestParam(value = "DateF", required = false, defaultValue = "") String dateF) {
//
//		log.info("[ SEMAINE CONTROLLER ] ~ [ FIND HORAIRE BY ID PERSONNEL AND DATE ]");
//		try {
//
//			if (periode.equals("") || type.equals("") || dateD.equals("") || dateF.equals("")) {
//				return ResponseEntity.badRequest().body(new MessageResponse("Error: paramètres incorects  !"));
//			}
//			for (UUID id : services) {
//				if (!serviceReporsitory.existsById(id)) {
//					return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found service with id  !"));
//				}
//			}
//
//			List<PKTimesheet> timesheets = timesheetService.createTimeSheet(services, dateD, dateF, periode);
//
//			if (type.equals("jour")) {
//				List<Journee> jours = new ArrayList<Journee>();
//				for (PKTimesheet timesheet : timesheets) {
//
//					Journee journee = new Journee();
//					List<Horaire> horaires = new ArrayList<Horaire>();
//					journee.setIdPersonnel(timesheet.getPersonnel().getId());
//					journee.setNom(timesheet.getPersonnel().getSNom());
//					journee.setPrenom(timesheet.getPersonnel().getSPrenom());
//					Horaire horaire = new Horaire();
//					horaire.setId(timesheet.getId());
//					horaire.setDateTimesheet(timesheet.getDateTimesheet());
//					horaire.setParty1He(timesheet.getSParty1He());
//					horaire.setParty2Hs(timesheet.getSParty2Hs());
//					horaire.setHeureSup(timesheet.getNHeureSup());
//					horaire.setJourTravaille(timesheet.getFJourTravaille());
//					horaire.setStatus(timesheet.getStatus());
//					horaires.add(horaire);
//					journee.setHoraires(horaires);
//					Journee last = jours.stream()
//							.filter(j -> j.getIdPersonnel().equals(timesheet.getPersonnel().getId())).findFirst()
//							.orElse(null);
//					if (last != null) {
//						int index = jours.indexOf(last);
//						last.getHoraires().add(horaire);
//						jours.set(index, last);
//					} else {
//
//						jours.add(journee);
//					}
//
//				}
//				return new ResponseEntity<>(jours, HttpStatus.OK);
//			} else if (type.equals("demi")) {
//				List<DemiJournee> jours = new ArrayList<DemiJournee>();
//				for (PKTimesheet timesheet : timesheets) {
//
//					DemiJournee journee = new DemiJournee();
//					List<DemiHoraire> horaires = new ArrayList<DemiHoraire>();
//					journee.setIdPersonnel(timesheet.getPersonnel().getId());
//					journee.setNom(timesheet.getPersonnel().getSNom());
//					journee.setPrenom(timesheet.getPersonnel().getSPrenom());
//					DemiHoraire horaire = new DemiHoraire();
//					horaire.setId(timesheet.getId());
//					horaire.setDateTimesheet(timesheet.getDateTimesheet());
//					horaire.setParty1He(timesheet.getSParty1He());
//					horaire.setParty1Hs(timesheet.getSParty1Hs());
//					horaire.setParty2He(timesheet.getSParty2He());
//					horaire.setParty2Hs(timesheet.getSParty2Hs());
//					horaire.setHeureSup(timesheet.getNHeureSup());
//					horaire.setJourTravaille(timesheet.getFJourTravaille());
//					horaire.setStatus(timesheet.getStatus());
//					horaires.add(horaire);
//					journee.setHoraires(horaires);
//					DemiJournee last = jours.stream()
//							.filter(j -> j.getIdPersonnel().equals(timesheet.getPersonnel().getId())).findFirst()
//							.orElse(null);
//					if (last != null) {
//						int index = jours.indexOf(last);
//						last.getHoraires().add(horaire);
//						jours.set(index, last);
//					} else {
//						jours.add(journee);
//					}
//				}
//				return new ResponseEntity<>(jours, HttpStatus.OK);
//			}
//			return new ResponseEntity<>(timesheets, HttpStatus.OK);
//
//		} catch (Exception ex) {
//			log.error("ERROR : ", ex.getMessage());
//			ex.printStackTrace();
//			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//	}


}
