package com.procheck.intranet.services.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKHoraire;
import com.procheck.intranet.models.PKModifierTimesheet;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKService;
import com.procheck.intranet.models.PKTimesheet;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.payload.request.ConfermationTs;
import com.procheck.intranet.payload.request.DemandeMTSFilter;
import com.procheck.intranet.payload.request.DemandeModificationTs;
import com.procheck.intranet.payload.request.DemiHoraire;
import com.procheck.intranet.payload.request.ModificationTs;
import com.procheck.intranet.payload.request.PersonnelFilterByService;
import com.procheck.intranet.payload.request.Timesheet;

import com.procheck.intranet.repository.JourFerieReporsitory;
import com.procheck.intranet.repository.ModificationTSRepository;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.repository.TimesheetReporsitory;
import com.procheck.intranet.security.services.IRoleService;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.IHoraireService;
import com.procheck.intranet.services.IPaysService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.ISemaineTravailService;
import com.procheck.intranet.services.IServiceService;
import com.procheck.intranet.services.ITimesheetService;
import com.procheck.intranet.services.specifications.DemandeModificationTsSpec;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TimesheetServiceImpl implements ITimesheetService {

	@Autowired
	TimesheetReporsitory timesheetReporsitory;
	@Autowired
	IHoraireService horaireService;

	@Autowired
	IUserDetailsService userService;

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
	IPaysService paysService;

	@Autowired
	JourFerieReporsitory jourFerieReporsitory;

	@Autowired
	IRoleService roleservice;

	@Autowired
	ModificationTSRepository modificationTSRepository;

	@Autowired
	DemandeModificationTsSpec demandeModificationTsSpec;

	@Override
	public PKTimesheet findByDDateTimesheetAndPersonnel(LocalDate date, UUID personne) {

		log.info("[ TIMESHEET SERVICE ] ~ [ FIND TIMENSHEET BY ID PERSONNEL AND DATE ]");
		return timesheetReporsitory.findByDateTimesheetAndPersonnel_id(date, personne);
	}

	@Override
	public boolean existsByDDateTimesheetAndPersonnel_id(LocalDate date, UUID personne) {
		log.info("[ TIMESHEET SERVICE ] ~ [ VERIFICATION TIMENSHEET BY ID PERSONNEL AND DATE ]");
		return timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, personne);
	}

	@Override
	public List<Timesheet> createTimeSheet(PersonnelFilterByService personnelFilter, String dateD, String dateF,
			String periode, UUID idUser) throws ParseException {
		log.info("[ TIMESHEET SERVICE ] ~ [ CREATION TIMESHEET SERVICES BY DATE AND PARAMETRAGE ]");
		List<PKPersonnel> personnels = personnelService.findPersonnelByServicesAndFilter(personnelFilter);

		List<Timesheet> timesheets = new ArrayList<Timesheet>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate localDateD = LocalDate.parse(dateD, formatter);

		Calendar calAt = Calendar.getInstance();

		List<String> roles = roleservice.getNameRoleByUser(userService.findOne(idUser));
//		boolean isResponsable = roles.contains("SUPERVISEUR");
		boolean isSuperviseur = roles.contains("SUPERVISEUR");
		for (PKPersonnel personnel : personnels) {

			if (periode.equals("journalier")) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(localDateD, personnel.getId())) {
					PKTimesheet time = timesheetReporsitory.findByDateTimesheetAndPersonnel_id(localDateD,
							personnel.getId());

					Timesheet timesheet = Outils.MapTimeToTimesheet(time, personnel, isSuperviseur);

					timesheets.add(timesheet);
				} else {

					if (jourFerieReporsitory.existsByPays_idAndDateJoureFerie(personnel.getPkPays().getId(),
							localDateD)) {

						PKTimesheet timesheet = Outils.NewTimesheet(personnel, localDateD, calAt, "holiday", 3, idUser);
						PKTimesheet ts = timesheetReporsitory.save(timesheet);
						Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperviseur);
						timesheets.add(time);

					} else {
						String str = localDateD.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
						log.info("DAY NAME :" + str);
//						PKSemaineTravail semaine = semaineTravailService
//								.findByPersonnels_idAndHoraireByDate(personnel.getId(), str);
						PKHoraire horaire = horaireService.findByJourIgnoreCaseAndSemaineTravails_Id(str,
								personnel.getSemaineTravail().getId());
						if (!Objects.equals(horaire, null)) {
							PKTimesheet timesheet = new PKTimesheet();
							timesheet.setPersonnel(personnel);
							timesheet.setDateTimesheet(localDateD);
							if (str.equals("dimanche")) {
								int weekOfYear = localDateD.get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
								timesheet.setSemaine("semaine" + weekOfYear);
							} else {
								int weekOfYear = localDateD.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
								timesheet.setSemaine("semaine" + weekOfYear);
							}
							timesheet.setSParty1He(horaire.getSParty1He());
							timesheet.setSParty1Hs(horaire.getSParty1Hs());
							timesheet.setSParty2He(horaire.getSParty2He());
							timesheet.setSParty2Hs(horaire.getSParty2Hs());
							timesheet.setNHeureTravaille(horaire.getHeurJournee());
							timesheet.setNHeureTotale("00:00");
							timesheet.setService(personnel.getService().getId());
							timesheet.setDCreatedAt(calAt);
							timesheet.setSCreatedBy(idUser);
							timesheet.setStatus("nouveau");
							timesheet.setBEnabled(true);
							timesheet.setBModifier(false);
							timesheet.setFJourTravaille(1);
							PKTimesheet ts = timesheetReporsitory.save(timesheet);
							Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperviseur);
							timesheets.add(time);
						} else {
							PKTimesheet timesheet = Outils.NewTimesheet(personnel, localDateD, calAt, "off", 0,
									idUser);
							PKTimesheet ts = timesheetReporsitory.save(timesheet);

							Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperviseur);
							timesheets.add(time);
						}
					}

				}
			} else if (periode.equals("Hebdomadaire") || periode.equals("mensuel")) {

				for (LocalDate date : Outils.getDatesBetween(dateD, dateF)) {

					if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, personnel.getId())) {
						PKTimesheet time = timesheetReporsitory.findByDateTimesheetAndPersonnel_id(date,
								personnel.getId());
						Timesheet timesheet = Outils.MapTimeToTimesheet(time, personnel, isSuperviseur);
						timesheets.add(timesheet);
					} else {

						if (jourFerieReporsitory.existsByPays_idAndDateJoureFerie(personnel.getPkPays().getId(),
								date)) {

							PKTimesheet timesheet = Outils.NewTimesheet(personnel, date, calAt, "holiday", 3, idUser);
							PKTimesheet ts = timesheetReporsitory.save(timesheet);
							Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperviseur);
							timesheets.add(time);

						} else {
							String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
							log.info("DAY NAME :" + str);
//							PKSemaineTravail semaine = semaineTravailService
//									.findByPersonnels_idAndHoraireByDate(personnel.getId(), str);
							PKHoraire horaire = horaireService.findByJourIgnoreCaseAndSemaineTravails_Id(str,
									personnel.getSemaineTravail().getId());

							if (!Objects.equals(horaire, null)) {
								PKTimesheet timesheet = new PKTimesheet();
								timesheet.setPersonnel(personnel);
								timesheet.setDateTimesheet(date);
								timesheet.setSParty1He(horaire.getSParty1He());
								timesheet.setSParty1Hs(horaire.getSParty1Hs());
								timesheet.setSParty2He(horaire.getSParty2He());
								timesheet.setSParty2Hs(horaire.getSParty2Hs());
								timesheet.setNHeureTravaille(horaire.getHeurJournee());
								timesheet.setNHeureTotale("00:00");
								if (str.equals("dimanche")) {
									int weekOfYear = localDateD.get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
									timesheet.setSemaine("semaine" + weekOfYear);
								} else {
									int weekOfYear = localDateD.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
									timesheet.setSemaine("semaine" + weekOfYear);
								}
								timesheet.setService(personnel.getService().getId());
								timesheet.setSCreatedBy(idUser);
								timesheet.setDCreatedAt(calAt);
								timesheet.setStatus("nouveau");
								timesheet.setBEnabled(true);
								timesheet.setBModifier(false);
								timesheet.setFJourTravaille(1);
								PKTimesheet ts = timesheetReporsitory.save(timesheet);
								Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperviseur);
								timesheets.add(time);
							} else {
								PKTimesheet timesheet = Outils.NewTimesheet(personnel, date, calAt, "off", 0,
										idUser);
								PKTimesheet ts = timesheetReporsitory.save(timesheet);
								Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperviseur);
								timesheets.add(time);
							}
						}
					}
				}
			}
		}
		return timesheets;
	}

	@Override
	public boolean existsById(UUID id) {

		return timesheetReporsitory.existsById(id);
	}

	@Override
	public List<Timesheet> validationTsByIds(UUID idUser, List<Timesheet> timesheets) throws ParseException {
		log.info("[ TIMESHEET SERVICE ] ~ [ VALIDATION TS ]");
		List<Timesheet> times = new ArrayList<Timesheet>();
		PKUser user = userService.findOne(idUser);
		for (Timesheet timesheet : timesheets) {

			PKPersonnel personnel = personnelService.findPersonnelById(timesheet.getIdPersonnel());
			PKService service = personnel.getService();
			System.out.println("service :" + service.getNameService());
			System.out.println(" validation Responsable TS :" + service.getActiveRespo());
			if (service.getActiveRespo().equals(false)) {
				for (DemiHoraire horaire : timesheet.getHoraires()) {
					if (timesheetReporsitory.existsById(horaire.getId()) && horaire.getJourTravaille() == 1) {
						PKTimesheet time = timesheetReporsitory.findById(horaire.getId()).get();
						String heursTotalDemi = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(horaire.getParty1He(),
								horaire.getParty1Hs(), horaire.getParty2He(), horaire.getParty2Hs()));
						String heursTotalJour = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(horaire.getParty1He(),
								time.getSParty1Hs(), time.getSParty2He(), horaire.getParty2Hs()));
						if (horaire.getMessage().equals("OK")) {

							if (!personnel.isBProjetTs()) {

								time.setSParty1He(horaire.getParty1He());
								time.setSParty2Hs(horaire.getParty2Hs());
								time.setNHeureTotale(heursTotalJour);
								if (timesheet.getType().equals("demi")) {
									time.setSParty1Hs(horaire.getParty1Hs());
									time.setSParty2He(horaire.getParty2He());
									time.setNHeureTotale(heursTotalDemi);
								}

							} else {
								time.setNHeureTotale(horaire.getHeursTotal());
							}
//							if(timesheet.isProjet()) {
//								time.setNHeureTotale(horaire.getHeursTotal());
//							}else {
//								time.setNHeureTotale(Outils.FormatageNbHeurs(Outils.getNbHeureDemi(horaire.getParty1He(), horaire.getParty1Hs(), horaire.getParty2He(), horaire.getParty2Hs())));
//							}
							time.setFJourTravaille(horaire.getJourTravaille());
							time.setNHeureSup(horaire.getHeureSup());
							time.setNAbsent(horaire.isAbsent());
							time.setSAbsenceMotif(horaire.getAbsenceMotif());
							time.setStatus("validé");
							time.setSValiderBy(idUser);
							timesheetReporsitory.save(time);
							horaire.setStatus("validé");
							horaire.setHeursTotal(time.getNHeureTotale());
						}
						times.add(timesheet);
					}
				}
			} else {

				if (serviceReporsitory.existsByIdAndCodeResponsable(service.getId(), user.getPersonnel().getId())) {
					for (DemiHoraire horaire : timesheet.getHoraires()) {
						if (timesheetReporsitory.existsById(horaire.getId()) && horaire.getJourTravaille() == 1) {
							PKTimesheet time = timesheetReporsitory.findById(horaire.getId()).get();
							String heursTotalDemi = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(horaire.getParty1He(),
									horaire.getParty1Hs(), horaire.getParty2He(), horaire.getParty2Hs()));
							String heursTotalJour = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(horaire.getParty1He(),
									time.getSParty1Hs(), time.getSParty2He(), horaire.getParty2Hs()));
							if (horaire.getMessage().equals("OK")) {
								if (!personnel.isBProjetTs()) {

									time.setSParty1He(horaire.getParty1He());
									time.setSParty2Hs(horaire.getParty2Hs());
									time.setNHeureTotale(heursTotalJour);
									if (timesheet.getType().equals("demi")) {
										time.setSParty1Hs(horaire.getParty1Hs());
										time.setSParty2He(horaire.getParty2He());
										time.setNHeureTotale(heursTotalDemi);
									}
								} else {
									time.setNHeureTotale(horaire.getHeursTotal());
								}
//								if (timesheet.isProjet()) {
//									time.setNHeureTotale(horaire.getHeursTotal());
//								} else {
//									time.setNHeureTotale(Outils.FormatageNbHeurs(
//											Outils.getNbHeureDemi(horaire.getParty1He(), horaire.getParty1Hs(),
//													horaire.getParty2He(), horaire.getParty2Hs())));
//								}
								time.setFJourTravaille(horaire.getJourTravaille());
								time.setNHeureSup(horaire.getHeureSup());
								time.setNAbsent(horaire.isAbsent());
								time.setSAbsenceMotif(horaire.getAbsenceMotif());
								time.setStatus("validé");
								time.setSValiderBy(idUser);
								timesheetReporsitory.save(time);
								horaire.setStatus("validé");
								horaire.setHeursTotal(time.getNHeureTotale());
							}
						}
					}
				}
				if (serviceReporsitory.existsByIdAndCodeSuperviseur(service.getId(), user.getPersonnel().getId())) {
					for (DemiHoraire horaire : timesheet.getHoraires()) {
						if (timesheetReporsitory.existsById(horaire.getId()) && horaire.getJourTravaille() == 1) {
							PKTimesheet time = timesheetReporsitory.findById(horaire.getId()).get();
							String heursTotalDemi = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(horaire.getParty1He(),
									horaire.getParty1Hs(), horaire.getParty2He(), horaire.getParty2Hs()));
							String heursTotalJour = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(horaire.getParty1He(),
									time.getSParty1Hs(), time.getSParty2He(), horaire.getParty2Hs()));

							if (!time.getStatus().equals("validé") && horaire.getMessage().equals("OK")) {

								if (!personnel.isBProjetTs()) {

									time.setSParty1He(horaire.getParty1He());
									time.setSParty2Hs(horaire.getParty2Hs());
									time.setNHeureTotale(heursTotalJour);
									if (timesheet.getType().equals("demi")) {
										time.setSParty1Hs(horaire.getParty1Hs());
										time.setSParty2He(horaire.getParty2He());
										time.setNHeureTotale(heursTotalDemi);
									}
								} else {
									time.setNHeureTotale(horaire.getHeursTotal());
								}
//								if (timesheet.isProjet()) {
//									time.setNHeureTotale(horaire.getHeursTotal());
//								} else {
//									time.setNHeureTotale(Outils.FormatageNbHeurs(
//											Outils.getNbHeureDemi(horaire.getParty1He(), horaire.getParty1Hs(),
//													horaire.getParty2He(), horaire.getParty2Hs())));
//								}
								time.setFJourTravaille(horaire.getJourTravaille());
								time.setNHeureSup(horaire.getHeureSup());
								time.setNAbsent(horaire.isAbsent());
								time.setSAbsenceMotif(horaire.getAbsenceMotif());
								time.setStatus("Confermé");
								time.setSValiderBy(idUser);
								timesheetReporsitory.save(time);
								horaire.setStatus("Confermé");
								horaire.setHeursTotal(time.getNHeureTotale());
							}
						}
					}
				}
			}
			times.add(timesheet);
		}
		return times;
	}

	@Override
	public List<Timesheet> enregistrementTsByIds(UUID idUser, List<Timesheet> timesheets) {
		log.info("[ TIMESHEET SERVICE ] ~ [ ENREGISTREMENT TS ]");

		List<Timesheet> times = new ArrayList<Timesheet>();
		for (Timesheet timesheet : timesheets) {
			for (DemiHoraire horaire : timesheet.getHoraires()) {
				if (timesheetReporsitory.existsById(horaire.getId())) {
					if (!horaire.getStatus().equals("validé") && horaire.getJourTravaille() == 1) {
						PKTimesheet time = timesheetReporsitory.findById(horaire.getId()).get();
						time.setSParty1He(horaire.getParty1He());
						time.setSParty2Hs(horaire.getParty2Hs());
						time.setFJourTravaille(horaire.getJourTravaille());
						time.setNHeureSup(horaire.getHeureSup());
						time.setNAbsent(horaire.isAbsent());
						time.setSAbsenceMotif(horaire.getAbsenceMotif());
						time.setStatus("en cours");
						if (timesheet.getType().equals("demi")) {
							time.setSParty1Hs(horaire.getParty1Hs());
							time.setSParty2He(horaire.getParty2He());
						}
						timesheetReporsitory.save(time);
						horaire.setStatus("en cours");
					}
				}
			}
			times.add(timesheet);
		}
		return times;
	}

	/**
	 * incorrect
	 */
	@Override
	public Timesheet findTimesheetPersonnel(UUID idPersonnel, String dateD, String dateF) throws ParseException {
		log.info("[ TIMESHEET SERVICE ] ~ [ FIND TS BY ID PERSONNEL AND TOW DATE ]");
		PKPersonnel personnel = personnelService.findPersonnelById(idPersonnel);
		List<String> roles = roleservice.getNameRoleByUser(userService.findOne(idPersonnel));
		boolean isResponsable = roles.contains("RESPONSABLE");
		List<Timesheet> timesheets = new ArrayList<Timesheet>();
		for (LocalDate date : Outils.getDatesBetween(dateD, dateF)) {

			PKTimesheet time = timesheetReporsitory.findByDateTimesheetAndPersonnel_id(date, personnel.getId());
			Timesheet timesheet = Outils.MapTimeToTimesheet(time, personnel, isResponsable);
			timesheets.add(timesheet);
		}

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
		return times.get(0);
	}

	@Override
	public void createDemande(UUID idUser, DemandeModificationTs demande) {
		log.info("[ TIMESHEET SERVICE ] ~ [ CREATE DEMANDE TS ]");

		PKTimesheet timesheet = timesheetReporsitory.findById(demande.getIdTimesheet()).get();
		timesheet.setBModifier(true);
		PKService service = serviceService.findServiceById(timesheet.getService());
		timesheetReporsitory.save(timesheet);
		PKModifierTimesheet modifierTs = new PKModifierTimesheet();
		LocalDate ldate = LocalDate.now();
		modifierTs.setDateDemande(ldate);
		PKUser user = userService.findOne(idUser);
		modifierTs.setDemandeur(user.getPersonnel().getSNom());
//		UUID code=UUID.fromString(service.getCodeResponsable().replace("R",null));
		PKPersonnel personnel = personnelReporsitory.getOne(service.getCodeResponsable());
		modifierTs.setRecepteur(personnel.getUser().getId());
		modifierTs.setTypeDemande("Modification TS");
		modifierTs.setStatus("en cours");
		modifierTs.setEmployee(timesheet.getPersonnel().getSNom() + " " + timesheet.getPersonnel().getSPrenom());
		modifierTs.setDateTS(timesheet.getDateTimesheet());
		modifierTs.setTimesheet(timesheet);

		modificationTSRepository.save(modifierTs);

	}

	@Override
	public void confermationDemandeTS(UUID idUser, List<ConfermationTs> confermations) {
		log.info("[ TIMESHEET SERVICE ] ~ [ CONFERMATION DEMANDE TS ]");

		for (ConfermationTs confermation : confermations) {
			PKModifierTimesheet demandeMTS = modificationTSRepository.findById(confermation.getIdDemande()).get();
			PKTimesheet timesheet = demandeMTS.getTimesheet();
			if (confermation.getStatus().equals("validé")) {

				demandeMTS.setSValiderBy(idUser);
				demandeMTS.setStatus("validé");
				modificationTSRepository.save(demandeMTS);
				timesheet.setSValiderBy(null);
				timesheet.setStatus("en cours");
				timesheet.setBModifier(false);
				timesheetReporsitory.save(timesheet);

			} else if (confermation.getStatus().equals("annulé")) {

				demandeMTS.setSValiderBy(idUser);
				demandeMTS.setStatus("annulé");
				modificationTSRepository.save(demandeMTS);
				timesheet.setBModifier(false);
				timesheetReporsitory.save(timesheet);
			}
		}

	}

	@Override
	public List<ModificationTs> findDemandeModificationTS(UUID idUser) {
		log.info("[ TIMESHEET SERVICE ] ~ [ GET DEMANDE TS BY ID USER]");

		PKUser user = userService.findOne(idUser);
		List<PKModifierTimesheet> listMTS = modificationTSRepository.findByRecepteur(user.getPersonnel().getId());

		List<ModificationTs> lists = new ArrayList<ModificationTs>();

		for (PKModifierTimesheet dmdMo : listMTS) {
			ModificationTs mod = new ModificationTs();
			mod.setIdDemande(dmdMo.getId());
			mod.setDateDemande(dmdMo.getDateDemande());
			mod.setDateTS(dmdMo.getTimesheet().getDateTimesheet());
			PKService service = serviceService.findServiceById(dmdMo.getTimesheet().getService());
			mod.setService(service.getNameService());
			mod.setSuperviseur(dmdMo.getDemandeur());
			mod.setEmployee(dmdMo.getTimesheet().getPersonnel().getSNom() + " "
					+ dmdMo.getTimesheet().getPersonnel().getSPrenom());
			mod.setStatus(dmdMo.getStatus());
			lists.add(mod);
		}

		return lists;
	}

	@Override
	public Page<PKModifierTimesheet> findDemandeMTSByFilter(DemandeMTSFilter dmdMTS, int size, int page) {
		log.info("[ TIMESHEET SERVICE ] ~ [ GET DEMANDE TS BY FILTER ]");
		return demandeModificationTsSpec.getDemandMTSsByFilter(dmdMTS, size, page);
	}

	@Override
	public List<Timesheet> createTimeSheetUser(UUID idUser, String dateD, String dateF, String periode)
			throws ParseException {
		log.info("[ TIMESHEET SERVICE ] ~ [ CREATION TIMESHEET USER BY DATE AND PARAMETRAGE ]");
		List<Timesheet> timesheets = new ArrayList<Timesheet>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate localDateD = LocalDate.parse(dateD, formatter);
		PKPersonnel personnel = userService.findOne(idUser).getPersonnel();
		Calendar calAt = Calendar.getInstance();
		List<String> roles = roleservice.getNameRoleByUser(userService.findOne(idUser));
		boolean isSuperAgent = roles.contains("EMP_GTS");

		if (periode.equals("journalier")) {

			if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(localDateD, personnel.getId())) {
				PKTimesheet time = timesheetReporsitory.findByDateTimesheetAndPersonnel_id(localDateD,
						personnel.getId());

				Timesheet timesheet = Outils.MapTimeToTimesheetUser(time, personnel, isSuperAgent);

				timesheets.add(timesheet);
			} else {

				if (jourFerieReporsitory.existsByPays_idAndDateJoureFerie(personnel.getPkPays().getId(), localDateD)) {

					PKTimesheet timesheet = Outils.NewTimesheet(personnel, localDateD, calAt, "holiday", 3, idUser);
					PKTimesheet ts = timesheetReporsitory.save(timesheet);
					Timesheet time = Outils.MapTimeToTimesheetUser(ts, personnel, isSuperAgent);
					timesheets.add(time);

				} else {
					String str = localDateD.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
					log.info("DAY NAME :" + str);

					PKHoraire horaire = horaireService.findByJourIgnoreCaseAndSemaineTravails_Id(str,
							personnel.getSemaineTravail().getId());
					if (!Objects.equals(horaire, null)) {
						PKTimesheet timesheet = new PKTimesheet();
						timesheet.setPersonnel(personnel);
						timesheet.setDateTimesheet(localDateD);
						if (str.equals("dimanche")) {
							int weekOfYear = localDateD.get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
							timesheet.setSemaine("semaine" + weekOfYear);
						} else {
							int weekOfYear = localDateD.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
							timesheet.setSemaine("semaine" + weekOfYear);
						}
						timesheet.setSParty1He(horaire.getSParty1He());
						timesheet.setSParty1Hs(horaire.getSParty1Hs());
						timesheet.setSParty2He(horaire.getSParty2He());
						timesheet.setSParty2Hs(horaire.getSParty2Hs());
						timesheet.setNHeureTravaille(horaire.getHeurJournee());
						timesheet.setNHeureTotale("00:00");
						timesheet.setService(personnel.getService().getId());
						timesheet.setDCreatedAt(calAt);
						timesheet.setSCreatedBy(idUser);
						timesheet.setStatus("nouveau");
						timesheet.setBEnabled(true);
						timesheet.setBModifier(false);
						timesheet.setFJourTravaille(1);
						PKTimesheet ts = timesheetReporsitory.save(timesheet);
						Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperAgent);
						timesheets.add(time);
					} else {
						PKTimesheet timesheet = Outils.NewTimesheet(personnel, localDateD, calAt, "off", 0, idUser);
						PKTimesheet ts = timesheetReporsitory.save(timesheet);

						Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperAgent);
						timesheets.add(time);
					}
				}

			}
		} else if (periode.equals("Hebdomadaire") || periode.equals("mensuel")) {

			for (LocalDate date : Outils.getDatesBetween(dateD, dateF)) {

				if (timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, personnel.getId())) {
					PKTimesheet time = timesheetReporsitory.findByDateTimesheetAndPersonnel_id(date, personnel.getId());
					Timesheet timesheet = Outils.MapTimeToTimesheet(time, personnel, isSuperAgent);
					timesheets.add(timesheet);
				} else {

					if (jourFerieReporsitory.existsByPays_idAndDateJoureFerie(personnel.getPkPays().getId(), date)) {

						PKTimesheet timesheet = Outils.NewTimesheet(personnel, date, calAt, "holiday", 3, idUser);
						PKTimesheet ts = timesheetReporsitory.save(timesheet);
						Timesheet time = Outils.MapTimeToTimesheet(ts, personnel, isSuperAgent);
						timesheets.add(time);

					} else {
						String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
						log.info("DAY NAME :" + str);

						PKHoraire horaire = horaireService.findByJourIgnoreCaseAndSemaineTravails_Id(str,
								personnel.getSemaineTravail().getId());

						if (!Objects.equals(horaire, null)) {
							PKTimesheet timesheet = new PKTimesheet();
							timesheet.setPersonnel(personnel);
							timesheet.setDateTimesheet(date);
							timesheet.setSParty1He(horaire.getSParty1He());
							timesheet.setSParty1Hs(horaire.getSParty1Hs());
							timesheet.setSParty2He(horaire.getSParty2He());
							timesheet.setSParty2Hs(horaire.getSParty2Hs());
							timesheet.setNHeureTravaille(horaire.getHeurJournee());
							timesheet.setNHeureTotale("00:00");
							if (str.equals("dimanche")) {
								int weekOfYear = localDateD.get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
								timesheet.setSemaine("semaine" + weekOfYear);
							} else {
								int weekOfYear = localDateD.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
								timesheet.setSemaine("semaine" + weekOfYear);
							}
							timesheet.setService(personnel.getService().getId());
							timesheet.setSCreatedBy(idUser);
							timesheet.setDCreatedAt(calAt);
							timesheet.setStatus("nouveau");
							timesheet.setBEnabled(true);
							timesheet.setBModifier(false);
							timesheet.setFJourTravaille(1);
							PKTimesheet ts = timesheetReporsitory.save(timesheet);
							Timesheet time = Outils.MapTimeToTimesheetUser(ts, personnel, isSuperAgent);
							timesheets.add(time);
						} else {
							PKTimesheet timesheet = Outils.NewTimesheet(personnel, date, calAt, "off", 0, idUser);
							PKTimesheet ts = timesheetReporsitory.save(timesheet);
							Timesheet time = Outils.MapTimeToTimesheetUser(ts, personnel, isSuperAgent);
							timesheets.add(time);
						}
					}
				}
			}
		}

		return timesheets;
	}

	@Override
	public Timesheet conferamtionTsEmp(UUID idUser, Timesheet timesheet) {
		log.info("[ TIMESHEET SERVICE ] ~ [ CONFERMATION TS BY EMPLOYEE ]");

		for (DemiHoraire horaire : timesheet.getHoraires()) {
			if (timesheetReporsitory.existsById(horaire.getId()) && horaire.getJourTravaille() == 1) {
				if (!horaire.getStatus().equals("validé") || !horaire.getStatus().equals("Confermé") ) {
					PKTimesheet time = timesheetReporsitory.findById(horaire.getId()).get();
					time.setSParty1He(horaire.getParty1He());
					time.setSParty2Hs(horaire.getParty2Hs());
					time.setFJourTravaille(horaire.getJourTravaille());
					time.setNHeureSup(horaire.getHeureSup());
					time.setNAbsent(horaire.isAbsent());
					time.setSAbsenceMotif(horaire.getAbsenceMotif());
					time.setStatus("Confermé");
					if (timesheet.getType().equals("demi")) {
						time.setSParty1Hs(horaire.getParty1Hs());
						time.setSParty2He(horaire.getParty2He());
					}
					timesheetReporsitory.save(time);
					horaire.setStatus("Confermé");
				}
			}
		}
		return timesheet;
	}

	@Override
	public Timesheet enregistrementTsEmp(UUID idUser, Timesheet timesheet) {
		log.info("[ TIMESHEET SERVICE ] ~ [ ENREGISTREMENT TS BY EMPLOYEE ]");

		for (DemiHoraire horaire : timesheet.getHoraires()) {
			if (timesheetReporsitory.existsById(horaire.getId()) && horaire.getJourTravaille() == 1) {
				if (!horaire.getStatus().equals("validé") || !horaire.getStatus().equals("Confermé")) {
					PKTimesheet time = timesheetReporsitory.findById(horaire.getId()).get();
					time.setSParty1He(horaire.getParty1He());
					time.setSParty2Hs(horaire.getParty2Hs());
					time.setFJourTravaille(horaire.getJourTravaille());
					time.setNHeureSup(horaire.getHeureSup());
					time.setNAbsent(horaire.isAbsent());
					time.setSAbsenceMotif(horaire.getAbsenceMotif());
					time.setStatus("en cours");
					if (timesheet.getType().equals("demi")) {
						time.setSParty1Hs(horaire.getParty1Hs());
						time.setSParty2He(horaire.getParty2He());
					}
					timesheetReporsitory.save(time);
					horaire.setStatus("en cours");
				}
			}
		}
		return timesheet;
	}

}
