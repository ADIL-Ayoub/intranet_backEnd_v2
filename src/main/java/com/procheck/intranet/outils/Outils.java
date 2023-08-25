package com.procheck.intranet.outils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.procheck.intranet.models.ETypeConge;
import com.procheck.intranet.models.ETypeGenerationTs;
import com.procheck.intranet.models.PKCheck;
import com.procheck.intranet.models.PKConge;
import com.procheck.intranet.models.PKDemande;
import com.procheck.intranet.models.PKHoraire;
import com.procheck.intranet.models.PKJoureFerie;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKProjetTimesheet;
import com.procheck.intranet.models.PKTimesheet;
import com.procheck.intranet.payload.request.DemiHoraire;
import com.procheck.intranet.payload.request.DetailTypeConge;
import com.procheck.intranet.payload.request.GenerationTs;
import com.procheck.intranet.payload.request.InfosPersonne;
import com.procheck.intranet.payload.request.JourFerie;
import com.procheck.intranet.payload.request.JourTravail;
import com.procheck.intranet.payload.request.MesConges;
import com.procheck.intranet.payload.request.MesDocuments;
import com.procheck.intranet.payload.request.Timesheet;
import com.procheck.intranet.payload.request.TimsheetByProjet;
import com.procheck.intranet.payload.request.TypeConge;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Outils {

	public static String TYPE1 = "text/csv";

	public static String TYPE2 = "application/vnd.ms-excel";

	
	public static boolean checkDExperation(Calendar dateExperation) {

		Calendar dateNow = Calendar.getInstance();

		if (dateExperation.before(dateNow)) {

			return false;
		}

		return true;
	}

	public static boolean isValid(String password, String pattern) {
		Pattern pattern1 = Pattern.compile(pattern);
		Matcher matcher = pattern1.matcher(password);
		return matcher.matches();
	}

	public static boolean checkNTenative(int tentative) {

		if (tentative % 3 == 0) {
			System.out.println("true " + tentative + ":" + tentative % 3);
			return true;
		}
		System.out.println("false " + tentative + ":" + tentative % 3);
		return false;
	}

	public static boolean checkFiveMinute(Calendar dateLastConx) {

		if (dateLastConx.before(Calendar.getInstance())) {

			return false;
		}

		return true;
	}

	public static boolean checkPassword(List<PKCheck> checks, String password) {

		PasswordEncoder encoder = new BCryptPasswordEncoder();

		if (checks != null) {
			for (PKCheck check : checks) {

				if (encoder.matches(password, check.getSLabel())) {

					return false;
				}
			}
		}
		return true;
	}

	public static boolean checkOldPassword(String password, String passwordold) {

		PasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.matches(password, passwordold);

	}

	public static ETypeGenerationTs checkTypeGenerationTS(String type) {

		if (type.toLowerCase().equals("manuel")) {
			return ETypeGenerationTs.Manuel;
		} else if (type.toLowerCase().equals("horaire")) {
			return ETypeGenerationTs.horaire;
		} else if (type.toLowerCase().equals("pointeuse")) {
			return ETypeGenerationTs.Pointeuse;
		}

		return null;
	}

	public static boolean checkPerimetre(String perimetre) {

		if (perimetre.equalsIgnoreCase("interne")) {
			return true;
		}
		if (perimetre.equalsIgnoreCase("externe")) {
			return true;
		}
		return false;
	}

	public static String checkIOrganisme(String type) {

		if (type.toLowerCase().equals("service")) {
			return "service";
		} else if (type.toLowerCase().equals("client")) {
			return "client";
		} else if (type.toLowerCase().equals("departement")) {
			return "departement";
		}

		return null;
	}

	public static boolean checkParametrageGenerationTS(GenerationTs generationTs) {

		if (generationTs.isFGenerationTs() && generationTs.sTypeGenerationTs.toLowerCase().equals("manuel")) {

			return false;

		} else if (!generationTs.isFGenerationTs()
				&& generationTs.sTypeGenerationTs.toLowerCase().equals("pointeuse")) {

			return false;

		} else if (!generationTs.isFGenerationTs() && generationTs.sTypeGenerationTs.toLowerCase().equals("horaire")) {

			return false;

		}

		return true;
	}

	public static ETypeConge checkTypConge(String type) {

		if (type.toLowerCase().equals("heur")) {
			return ETypeConge.Heur;
		} else if (type.toLowerCase().equals("jeur")) {
			return ETypeConge.Jour;
		} else {
			return ETypeConge.None;
		}
	}

	public static boolean hasCSVFormat(MultipartFile file) {

		if (!TYPE1.equals(file.getContentType()) || !TYPE2.equals(file.getContentType())) {
			return false;
		}

		return true;
	}

	public static boolean checkExtention(String file) {
		System.out.println("********************" + file.split(".")[1]);
		if (!file.contains(".csv")) {
			return false;
		}

		return true;

	}

	public static List<LocalDate> getDatesBetween(String dateD, String dateF) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate startDate = LocalDate.parse(dateD, formatter);
		LocalDate endDate = LocalDate.parse(dateF, formatter).plusDays(1);
		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> startDate.plusDays(i))
				.collect(Collectors.toList());
	}

	public static List<LocalDate> getDatesBetweenConge(String dateD, String dateF) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startDate = LocalDate.parse(dateD, formatter);
		LocalDate endDate = LocalDate.parse(dateF, formatter);
		long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> startDate.plusDays(i))
				.collect(Collectors.toList());
	}
	
	
	public static Map<UUID, List<PKTimesheet>> listToMap(List<PKTimesheet> timesheets) {
		Map<UUID, List<PKTimesheet>> map = new HashMap();

		for (PKTimesheet timesheet : timesheets) {

			List<PKTimesheet> list = map.get(timesheet.getPersonnel().getId());
			if (list == null) {
				list = new ArrayList<PKTimesheet>();
			}
			list.add(timesheet);
			map.put(timesheet.getPersonnel().getId(), list);

		}

		return map;
	}

	public static long getNbHeureJour(String PHE, String PHS) throws ParseException {
		long nbHeure = 0;
		if (PHE == null || PHE.equals("") || PHE.equals("-1") || PHS == null || PHS.equals("") || PHS.equals("-1"))
			nbHeure = 0;
		else {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");

			Date startDate = format.parse(PHE);// Set start date
			Date endDate = format.parse(PHS);// Set end date
			long duration = endDate.getTime() - startDate.getTime();
			if (duration < 0)
				duration += 1000 * 60 * 60 * 24;
			long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
			nbHeure = diffInMinutes;
		}
		return nbHeure;
	}

	public static long getNbHeureDemi(String P1HE, String P1HS, String P2HE, String P2HS) throws ParseException {

		long party1 = getNbHeureJour(P1HE, P1HS);
		long party2 = getNbHeureJour(P2HE, P2HS);

		return party1 + party2;

	}

	public static String TotaleHeur(List<TimsheetByProjet> listTSP) throws ParseException {
		long nbHeure = 0;

		for (TimsheetByProjet timsheetByProjet : listTSP) {
			if (timsheetByProjet.getTime() == null || timsheetByProjet.getTime().equals("")
					|| timsheetByProjet.getTime().equals("-1"))
				nbHeure = 0;
			else {
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");

				Date time = format.parse(timsheetByProjet.getTime());
				long duration = time.getTime();
				if (duration < 0)
					duration += 1000 * 60 * 60 * 24;
				long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
				nbHeure = nbHeure + diffInMinutes;
			}

		}
		return FormatageNbHeurs(nbHeure);

	}

	public static String FormatageNbHeurs(long minutes) {

		Duration dur = Duration.ofMinutes(minutes);
		String heur = String.format(Locale.forLanguageTag("en_IN"), "%02d:%02d", dur.toHours(), dur.toMinutes() % 60);

		return heur;
	}
	
	public static Timesheet MapTimeToTimesheetUser(PKTimesheet timesheet, PKPersonnel personnel,boolean isSuperAgent)
			throws ParseException {

		Timesheet time = new Timesheet();
		time.setIdPersonnel(personnel.getId());
		time.setNom(personnel.getNom());
		time.setPrenom(personnel.getPrenom());
		time.setCin(personnel.getCin());
		time.setPoste(personnel.getSPoste());
		time.setMatrucule(personnel.getSMatruculePaie());
		time.setType(personnel.getType());
		time.setProjet(personnel.isBProjetTs());

		List<DemiHoraire> demiHoraires = new ArrayList<DemiHoraire>();

		DemiHoraire demiHoraire = new DemiHoraire();
		String str = timesheet.getDateTimesheet().format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
		demiHoraire.setJourName(str);
		if (str.equals("dimanche")) {
			int weekOfYear = timesheet.getDateTimesheet().get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
			demiHoraire.setSemaine("semaine" + weekOfYear);
		} else {
			int weekOfYear = timesheet.getDateTimesheet().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
			demiHoraire.setSemaine("semaine" + weekOfYear);
		}
		demiHoraire.setId(timesheet.getId());
		demiHoraire.setDateTimesheet(timesheet.getDateTimesheet());

		if (!personnel.isBProjetTs()) {
			demiHoraire.setParty1He(timesheet.getSParty1He());
			if (personnel.getType().equals("demi")) {
				demiHoraire.setParty1Hs(timesheet.getSParty1Hs());
				demiHoraire.setParty2He(timesheet.getSParty2He());
			}
			demiHoraire.setParty2Hs(timesheet.getSParty2Hs());
		}
//		demiHoraire.setHeursJour(timesheet.getNHeureTravaille());
//		String heurJournee = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(timesheet.getSParty1He(),
//				timesheet.getSParty1Hs(), timesheet.getSParty2He(), timesheet.getSParty2Hs()));
		demiHoraire.setHeursJour(timesheet.getNHeureTravaille());

		if (!personnel.isBProjetTs() || timesheet.getStatus().equals("validé")) {
			demiHoraire.setMessage("OK");
		} else {
			demiHoraire.setMessage("KO");
		}
		demiHoraire.setAbsent(timesheet.isNAbsent());
		demiHoraire.setAbsenceMotif(timesheet.getSAbsenceMotif());

		demiHoraire.setHeureSup(timesheet.getNHeureSup());
		demiHoraire.setJourTravaille(timesheet.getFJourTravaille());
		demiHoraire.setStatus(timesheet.getStatus());

		if ( isSuperAgent == true && timesheet.getStatus().equals("validé")) {
			demiHoraire.setEnabled(false);
		} else {
			demiHoraire.setEnabled(timesheet.isBEnabled());
		}
		demiHoraire.setModifier(timesheet.isBModifier());
		
		List<TimsheetByProjet> timeProjets = new ArrayList<TimsheetByProjet>();
		if (!Objects.equals(timesheet.getProjetTimesheets(), null)) {
			for (PKProjetTimesheet pt : timesheet.getProjetTimesheets()) {
				TimsheetByProjet tp = new TimsheetByProjet();
				tp.setId(pt.getId());
				tp.setProjet(pt.getProjet());
				tp.setTime(pt.getTime());
				tp.setDescription(pt.getDescription());
				tp.setIdTimesheet(timesheet.getId());
				timeProjets.add(tp);

			}
		}
		if(personnel.isBProjetTs()) {
			if (timeProjets.isEmpty()) {
				demiHoraire.setHeursTotal("00:00");
			} else {
				demiHoraire.setHeursTotal(Outils.TotaleHeur(timeProjets));
			}
		}else {
			demiHoraire.setHeursTotal(timesheet.getNHeureTotale());
		}
		demiHoraire.setProjetTS(timeProjets);
		demiHoraires.add(demiHoraire);
		time.setHoraires(demiHoraires);

		return time;
	}

	public static Timesheet MapTimeToTimesheet(PKTimesheet timesheet, PKPersonnel personnel, boolean isSuperviseur)
			throws ParseException {

		Timesheet time = new Timesheet();
		time.setIdPersonnel(personnel.getId());
		time.setNom(personnel.getNom());
		time.setPrenom(personnel.getPrenom());
		time.setCin(personnel.getCin());
		time.setPoste(personnel.getSPoste());
		time.setMatrucule(personnel.getSMatruculePaie());
		time.setType(personnel.getType());
		time.setProjet(personnel.isBProjetTs());

		List<DemiHoraire> demiHoraires = new ArrayList<DemiHoraire>();

		DemiHoraire demiHoraire = new DemiHoraire();
		String str = timesheet.getDateTimesheet().format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
		demiHoraire.setJourName(str);
		if (str.equals("dimanche")) {
			int weekOfYear = timesheet.getDateTimesheet().get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
			demiHoraire.setSemaine("semaine" + weekOfYear);
		} else {
			int weekOfYear = timesheet.getDateTimesheet().get(ChronoField.ALIGNED_WEEK_OF_YEAR);
			demiHoraire.setSemaine("semaine" + weekOfYear);
		}
		demiHoraire.setId(timesheet.getId());
		demiHoraire.setDateTimesheet(timesheet.getDateTimesheet());

		if (!personnel.isBProjetTs()) {
			demiHoraire.setParty1He(timesheet.getSParty1He());
			if (personnel.getType().equals("demi")) {
				demiHoraire.setParty1Hs(timesheet.getSParty1Hs());
				demiHoraire.setParty2He(timesheet.getSParty2He());
			}
			demiHoraire.setParty2Hs(timesheet.getSParty2Hs());
		}
//		demiHoraire.setHeursJour(timesheet.getNHeureTravaille());
//		String heurJournee = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(timesheet.getSParty1He(),
//				timesheet.getSParty1Hs(), timesheet.getSParty2He(), timesheet.getSParty2Hs()));
		demiHoraire.setHeursJour(timesheet.getNHeureTravaille());

		if (!personnel.isBProjetTs() || timesheet.getStatus().equals("validé")) {
			demiHoraire.setMessage("OK");
		} else {
			demiHoraire.setMessage("KO");
		}
		demiHoraire.setAbsent(timesheet.isNAbsent());
		demiHoraire.setAbsenceMotif(timesheet.getSAbsenceMotif());

		demiHoraire.setHeureSup(timesheet.getNHeureSup());
		demiHoraire.setJourTravaille(timesheet.getFJourTravaille());
		demiHoraire.setStatus(timesheet.getStatus());

		if (isSuperviseur == true && timesheet.getStatus().equals("validé")) {
			demiHoraire.setEnabled(false);
		} else {
			demiHoraire.setEnabled(timesheet.isBEnabled());
		}
		demiHoraire.setModifier(timesheet.isBModifier());
		
		List<TimsheetByProjet> timeProjets = new ArrayList<TimsheetByProjet>();
		if (!Objects.equals(timesheet.getProjetTimesheets(), null)) {
			for (PKProjetTimesheet pt : timesheet.getProjetTimesheets()) {
				TimsheetByProjet tp = new TimsheetByProjet();
				tp.setId(pt.getId());
				tp.setProjet(pt.getProjet());
				tp.setTime(pt.getTime());
				tp.setDescription(pt.getDescription());
				tp.setIdTimesheet(timesheet.getId());
				timeProjets.add(tp);

			}
		}
		if(personnel.isBProjetTs()) {
			if (timeProjets.isEmpty()) {
				demiHoraire.setHeursTotal("00:00");
			} else {
				demiHoraire.setHeursTotal(Outils.TotaleHeur(timeProjets));
			}
		}else {
			demiHoraire.setHeursTotal(timesheet.getNHeureTotale());
		}
		demiHoraire.setProjetTS(timeProjets);
		demiHoraires.add(demiHoraire);
		time.setHoraires(demiHoraires);

		return time;
	}

	public static PKTimesheet NewTimesheet(PKPersonnel personnel, LocalDate date, Calendar calAt, String status,
			int jourTravail, UUID idUser) throws ParseException {

		PKTimesheet timesheet = new PKTimesheet();
		timesheet.setPersonnel(personnel);
		timesheet.setDateTimesheet(date);
		timesheet.setSParty1He("00:00");
		timesheet.setSParty1Hs("00:00");
		timesheet.setSParty2He("00:00");
		timesheet.setSParty2Hs("00:00");
		String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
		if (str.equals("dimanche")) {
			int weekOfYear = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
			timesheet.setSemaine("semaine" + weekOfYear);
		} else {
			int weekOfYear = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
			timesheet.setSemaine("semaine" + weekOfYear);
		}
		timesheet.setService(personnel.getService().getId());
		String heurJournee = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(timesheet.getSParty1He(),
				timesheet.getSParty1Hs(), timesheet.getSParty2He(), timesheet.getSParty2Hs()));
		timesheet.setNHeureTravaille(heurJournee);
//		timesheet.setNHeureTravaille(jourTravail);
		timesheet.setSCreatedBy(idUser);
		timesheet.setDCreatedAt(calAt);
		timesheet.setNAbsent(false);
		timesheet.setStatus(status);
		timesheet.setFJourTravaille(jourTravail);

		return timesheet;
	}
	public static PKTimesheet TimesheetConge(PKTimesheet timesheet,PKPersonnel  personnel,LocalDate date) throws ParseException {

		
		timesheet.setPersonnel(personnel);
		timesheet.setDateTimesheet(date);
		timesheet.setSParty1He("00:00");
		timesheet.setSParty1Hs("00:00");
		timesheet.setSParty2He("00:00");
		timesheet.setSParty2Hs("00:00");
		String str = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.FRENCH));
		if (str.equals("dimanche")) {
			int weekOfYear = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR) - 1;
			timesheet.setSemaine("semaine" + weekOfYear);
		} else {
			int weekOfYear = date.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
			timesheet.setSemaine("semaine" + weekOfYear);
		}
		timesheet.setService(personnel.getService().getId());
		String heurJournee = Outils.FormatageNbHeurs(Outils.getNbHeureDemi(timesheet.getSParty1He(),
				timesheet.getSParty1Hs(), timesheet.getSParty2He(), timesheet.getSParty2Hs()));
		timesheet.setNHeureTravaille(heurJournee);
//		timesheet.setNHeureTravaille(jourTravail);
//		timesheet.setSCreatedBy(idUser);
		timesheet.setDCreatedAt(Calendar.getInstance() );
		timesheet.setNAbsent(false);
		timesheet.setStatus("conge");
		timesheet.setFJourTravaille(3);

		return timesheet;
	}
	public static boolean CheckTypeConge(TypeConge typeConge) {

		if (typeConge.isHeur() == typeConge.isJour()) {
			return false;
		}
		if (typeConge.getMax() < typeConge.getMin()) {
			return false;
		}
		for (DetailTypeConge detailTypeConge : typeConge.getDetaileConges()) {

			if (detailTypeConge.getMax() < detailTypeConge.getMin()) {
				return false;
			}
		}
		return true;
	}

	public static ByteArrayInputStream generPdfTimesheetByAgent(List<PKTimesheet> timesheets) {

		Document document = new Document();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			String contantQRcode = "";
			PKTimesheet fistTimesheet = timesheets.get(0);
			PKTimesheet lastTimesheet = timesheets.get(timesheets.size() - 1);

			PdfWriter.getInstance(document, bout);
			document.open();

			Paragraph paragraph0 = new Paragraph(" ");
			paragraph0.setSpacingAfter(30);
			document.add(paragraph0);
			
			Paragraph paragraph1 = new Paragraph(
					fistTimesheet.getPersonnel().getService().getClient().getShortNameClient() + " : "
							+ fistTimesheet.getPersonnel().getService().getNameService());
			paragraph1.setSpacingAfter(50);
			document.add(paragraph1);
			contantQRcode += fistTimesheet.getPersonnel().getService().getClient().getShortNameClient() + " : "
					+ fistTimesheet.getPersonnel().getService().getNameService();
			contantQRcode += "\n";

			PdfPTable table = new PdfPTable(2);
			Paragraph left = new Paragraph(
					fistTimesheet.getPersonnel().getNom().toUpperCase() + " " + fistTimesheet.getPersonnel().getPrenom());
			PdfPCell cell = new PdfPCell();
			cell.setPadding(0);
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setExtraParagraphSpace(0);
			cell.setRightIndent(0);
			cell.addElement(left);
			table.addCell(cell);
			//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Paragraph right = new Paragraph("Du " + fistTimesheet.getDateTimesheet() + " Au "
					+ lastTimesheet.getDateTimesheet());
			cell = new PdfPCell();
			cell.setPadding(0);
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setExtraParagraphSpace(0);
			cell.setRightIndent(0);
			cell.addElement(right);
			table.addCell(cell);
			document.add(table);
			contantQRcode += fistTimesheet.getPersonnel().getNom().toUpperCase() + " "
					+ fistTimesheet.getPersonnel().getPrenom();
			contantQRcode += "\n";
			contantQRcode += "Du " + fistTimesheet.getDateTimesheet() + " Au "
					+ lastTimesheet.getDateTimesheet();
			contantQRcode += "\n";
			Paragraph paragraph5 = new Paragraph("TIME SHEET");
			paragraph5.setAlignment(Element.ALIGN_CENTER);
			paragraph5.setSpacingBefore(30);
			paragraph5.setSpacingAfter(30);
			document.add(paragraph5);

//			String totalH = "00:00";
			int totalHS = 0; 
			double totalJoursTravaille = 0.0;
			table = new PdfPTable(5);

			cell = new PdfPCell(new Paragraph(" "));
			cell.setRowspan(2);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("Partie 1"));
			cell.setColspan(2);
			table.addCell(cell);
			cell = new PdfPCell(new Paragraph("Partie 2"));
			cell.setColspan(2);
			table.addCell(cell);

			table.addCell(new PdfPCell(new Paragraph("Heure d'entrée")));
			table.addCell(new PdfPCell(new Paragraph("Heure sortie")));
			table.addCell(new PdfPCell(new Paragraph("Heure d'entrée")));
			table.addCell(new PdfPCell(new Paragraph("Heure sortie")));

			for (PKTimesheet t : timesheets) {
				table.addCell(new PdfPCell(new Paragraph(""+t.getDateTimesheet())));
				if (t.getSParty1He() == null && t.getSParty1Hs() == null) {
					cell = new PdfPCell(new Paragraph(t.getSAbsenceType() == null ? "absent" : t.getSAbsenceType()));
					cell.setColspan(2);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
				} else {
					table.addCell(new PdfPCell(new Paragraph(t.getSParty1He())));
					table.addCell(new PdfPCell(new Paragraph(t.getSParty1Hs())));
				}
				if (t.getSParty2He() == null && t.getSParty2Hs() == null) {
					cell = new PdfPCell(new Paragraph(t.getSAbsenceType() == null ? "absent" : t.getSAbsenceType()));
					cell.setColspan(2);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
				} else {
					table.addCell(new PdfPCell(new Paragraph(t.getSParty2He())));
					table.addCell(new PdfPCell(new Paragraph(t.getSParty2Hs())));
				}

				totalHS += t.getNHeureSup();
				totalJoursTravaille += t.getFJourTravaille();
//				totalH =TotaleHeursPDF(timesheets) ;

			}

			String totalHeureSup = fromMinutesToHHmm(totalHS);
			String totalHeures = TotaleHeursPDF(timesheets);
			cell = new PdfPCell();
			cell.setColspan(2);
			cell.setPadding(4);
			cell.addElement(new Paragraph("total jours travaillés"));
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setPadding(4);
			cell.addElement(new Paragraph("total heures"));
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setColspan(2);
			cell.setPadding(4);
			cell.addElement(new Paragraph("total heures sup"));
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setColspan(2);
			cell.setPadding(4);
			cell.addElement(new Paragraph(totalJoursTravaille + " j"));
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setPadding(4);
			cell.addElement(new Paragraph(totalHeures + ""));
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setColspan(2);
			cell.setPadding(4);
			cell.addElement(new Paragraph(totalHeureSup + ""));
			table.addCell(cell);

			document.add(table);
			contantQRcode += "total jours travailles: " + totalJoursTravaille + " j";
			contantQRcode += "\n";
			contantQRcode += "total heures: " + totalHeures;
			contantQRcode += "\n";
			contantQRcode += "total heures sup: " + totalHeureSup;
			contantQRcode += "\n";
			Paragraph paragraph7 = new Paragraph(" ");
			paragraph7.setSpacingAfter(50);
			document.add(paragraph7);

			table = new PdfPTable(2);
			table.setWidthPercentage(100);
			Paragraph left1 = new Paragraph("VISA RESPONSABLE:  		");
			left1.setAlignment(Element.ALIGN_LEFT);
			cell = new PdfPCell();
			cell.setPadding(5);
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.addElement(left1);
			table.addCell(cell);

			Paragraph right1 = new Paragraph("DATE: " + new Date());
			right1.setAlignment(Element.ALIGN_RIGHT);
			cell = new PdfPCell();
			cell.setPadding(5);
			cell.setBorder(0);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.addElement(right1);
			table.addCell(cell);
			document.add(table);

			contantQRcode += "DATE: " + new Date();
			BarcodeQRCode barcodeQRCode = new BarcodeQRCode(contantQRcode, 60, 60, null);
			Image codeQrImage = barcodeQRCode.getImage();
			codeQrImage.scaleAbsolute(100, 100);
			codeQrImage.setAbsolutePosition(450, 700);
			document.add(codeQrImage);

			document.close();
		} catch (Exception e) {
			log.error("", e);
		}
		return new ByteArrayInputStream(bout.toByteArray());
	}
	
	//load data into csv
    public static ByteArrayInputStream load(final List<PKTimesheet> timesheets) {
        return generPdfTimesheetByAgent(timesheets);
    }
	
	public static String TotaleHeursPDF(List<PKTimesheet> listTSP) throws ParseException {
		long nbHeure = 0;

		for (PKTimesheet timsheetByProjet : listTSP) {
			if (timsheetByProjet.getNHeureTotale() == null || timsheetByProjet.getNHeureTotale().equals("")
					|| timsheetByProjet.getNHeureTotale().equals("-1"))
				nbHeure = 0;
			else {
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");

				Date time = format.parse(timsheetByProjet.getNHeureTotale());
				long duration = time.getTime();
				if (duration < 0)
					duration += 1000 * 60 * 60 * 24;
				long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
				nbHeure = nbHeure + diffInMinutes;
			}

		}
		return FormatageNbHeurs(nbHeure);

	}
	
	public static String fromMinutesToHHmm(int minutes) {
		long hours = TimeUnit.MINUTES.toHours(Long.valueOf(minutes));
		long remainMinutes = minutes - TimeUnit.HOURS.toMinutes(hours);
		return String.format("%02d:%02d", hours, remainMinutes);
	}
	
	
	
	public static Float nbrJours() {
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    try {
        Date dateAvant = sdf.parse("02/25/2012");
        Date dateApres = sdf.parse("03/31/2012");
        long diff = dateApres.getTime() - dateAvant.getTime();
        float res = (diff / (1000*60*60*24));
        System.out.println("Nombre de jours entre les deux dates est: "+res);
        return res;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
	
	}

	public static  List<MesConges> MapDemandeToConges(List<PKDemande> demandes) {

		List<MesConges> conges=new ArrayList<MesConges>();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		for (PKDemande demande : demandes) {
			MesConges conge=new MesConges();
			conge.setIdDemande(demande.getId());
			conge.setDemandeur(demande.getCodeDemandeur());
			//mon code
			conge.setTypeDemande(demande.getTypedemande().getNameTypeDemande());
			conge.setStatutDemande(demande.getStatus());
			//
			conge.setDateDemande(demande.getDDateCreation().format(dateTimeFormatter));
			conge.setIdPersonnel(demande.getPersonnel().getId());
			conge.setNom(demande.getPersonnel().getNom());
			conge.setPrenom(demande.getPersonnel().getPrenom());
			for (PKConge c : demande.getConges()) {
//				conge.setIdConge(c.getId());
				conge.setTypeConge(c.getTypeConge().getTypeConge());
				conge.setDateDebut(c.getDateDebut().format(dateTimeFormatter));
				conge.setDateReprise(c.getDateReprise().format(dateTimeFormatter));
				conge.setNbrJours(c.getNombreJour());
				conge.setStatus(c.getStatus());
				conge.setIdTypeConge(c.getTypeConge().getId());
				//mon code
				conge.setNomConge(c.getName());
				conge.setDescription(c.getDescription());
				//
			}
			conges.add(conge);

		}

		return conges;
	}

	public static List<MesDocuments> MapDemandeToDocument(List<PKDemande> demandes) {
		// TODO Auto-generated method stub
		return null;
	}

	public static InfosPersonne MapToInfosPersonne(String name, PKPersonnel beneficiare, List<PKJoureFerie> joureFeries,
			List<PKHoraire> jourTravail, List<PKConge> conges) {
		
		InfosPersonne infos=new InfosPersonne();
		infos.setIdBeneficiaire(beneficiare.getId());
		infos.setSolde(beneficiare.getFNbJourConge());
		
		List<JourFerie> joursFeries=new ArrayList<JourFerie>();
		
		for (PKJoureFerie jourFerie : joureFeries) {
			
			JourFerie jf=new JourFerie();
			
			jf.setName(jourFerie.getSDescriptionJoureFerie());
			jf.setDate(jourFerie.getDateJoureFerie());

			joursFeries.add(jf);
		}
		
		infos.setJoursFeries(joursFeries);
		
		
		List<JourTravail> joursTravail=new ArrayList<JourTravail>();
		
		for (PKHoraire horaire : jourTravail) {
			
			JourTravail jt=new JourTravail();
			
			jt.setName(horaire.getJour());
			
			joursTravail.add(jt);
			
		}
		
		infos.setJourTravail(joursTravail);
		
		List<LocalDate> dateConges=new ArrayList<LocalDate>();
		
		for (PKConge conge : conges) {
			
			LocalDate date=LocalDate.now().minusMonths(1);
			
//			System.out.println("date - month : "+date+
//			"\n date debut conge :"+ conge.getDateDebut() +
//			"\n condition : "+ conge.getDateDebut().isBefore(date));
			
			
			if(conge.getDateDebut().isAfter(date)) {
				
			List<LocalDate> ds=getDatesBetweenConge(conge.getDateDebut().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), conge.getDateReprise().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			
			dateConges.addAll(ds);
			}
		}	
		
		infos.setDateConges(dateConges);
		
		
		return infos;
	}
	
  
}
