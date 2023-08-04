package com.procheck.intranet.services.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKProjetTimesheet;

import com.procheck.intranet.outils.Outils;

import com.procheck.intranet.payload.request.TimsheetByProjet;
import com.procheck.intranet.repository.ProjetTimesheetRepository;
import com.procheck.intranet.repository.TimesheetReporsitory;
import com.procheck.intranet.services.IProjetTimesheet;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjetTimesheetService implements IProjetTimesheet {

	@Autowired
	ProjetTimesheetRepository projetTimesheetRepository;

	@Autowired
	TimesheetReporsitory timesheetReporsitory;

	@Override
	public String save(UUID id, List<TimsheetByProjet> projetTimesheets) throws ParseException {
		log.info("[SERVICE PROJET TIMSHEET] [SAVE TIMESHEET PROJETS ]");
		List<TimsheetByProjet> listTSP = new ArrayList<TimsheetByProjet>();
		for (TimsheetByProjet timsheetByProjet : projetTimesheets) {
//			if (!projetTimesheetRepository.existsByIdProjetAndTimesheet_id(timsheetByProjet.getIdProjet(),
//					timsheetByProjet.getIdTimesheet())) {
				PKProjetTimesheet projetTs = new PKProjetTimesheet();
				projetTs.setProjet(timsheetByProjet.getProjet());
				projetTs.setTime(timsheetByProjet.getTime());
				projetTs.setDescription(timsheetByProjet.getDescription());
				projetTs.setTimesheet(timesheetReporsitory.findById(timsheetByProjet.getIdTimesheet()).get());
				projetTs.setDCreatedAt(Calendar.getInstance());
				projetTs.setSCreatedBy(id);
				projetTimesheetRepository.save(projetTs);

//			}
			listTSP.add(timsheetByProjet);
		}

		return Outils.TotaleHeur(listTSP);

	}

	@Override
	public PKProjetTimesheet update(UUID id, TimsheetByProjet projetTimesheet) {
		log.info("[SERVICE PROJET TIMSHEET] [UPDATE PROJET TIMESHEET]");
		PKProjetTimesheet timesheetProjet = projetTimesheetRepository.findById(projetTimesheet.getId()).get();
		timesheetProjet.setProjet(projetTimesheet.getProjet());
		timesheetProjet.setTime(projetTimesheet.getTime());
		timesheetProjet.setDescription(projetTimesheet.getDescription());
		timesheetProjet.setDCreatedAt(Calendar.getInstance());
		timesheetProjet.setSCreatedBy(id);
		timesheetProjet.setTimesheet(timesheetReporsitory.findById(projetTimesheet.idTimesheet).get());
		return projetTimesheetRepository.save(timesheetProjet);
	}

	@Override
	public void delete(UUID id) {
		log.info("[SERVICE PROJET TIMSHEET] [DELETE PROJET TIMESHEET]");
		projetTimesheetRepository.deleteById(id);
	}

	@Override
	public boolean existsById(UUID id) {
		log.info("[SERVICE PROJET TIMSHEET] [EXISTS BY ID ]");
		return projetTimesheetRepository.existsById(id);
	}

}
