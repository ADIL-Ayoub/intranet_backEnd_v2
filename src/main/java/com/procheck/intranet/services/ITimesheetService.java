package com.procheck.intranet.services;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.procheck.intranet.models.PKModifierTimesheet;
import com.procheck.intranet.models.PKTimesheet;
import com.procheck.intranet.payload.request.ConfermationTs;
import com.procheck.intranet.payload.request.DemandeMTSFilter;
import com.procheck.intranet.payload.request.DemandeModificationTs;
import com.procheck.intranet.payload.request.ModificationTs;
import com.procheck.intranet.payload.request.PersonnelFilterByService;
import com.procheck.intranet.payload.request.Timesheet;

public interface ITimesheetService {

	PKTimesheet findByDDateTimesheetAndPersonnel(LocalDate date,UUID personne);
	boolean existsByDDateTimesheetAndPersonnel_id(LocalDate date,UUID personne);
	
	Timesheet findTimesheetPersonnel(UUID idPersonnel,String dateD,String dateF) throws ParseException ;
	
	List<Timesheet> createTimeSheet(PersonnelFilterByService personnel, String dateD,String dateF, String periode,UUID idUser)throws ParseException;
	
	boolean existsById(UUID id);
	
	List<Timesheet> validationTsByIds(UUID idUser,List<Timesheet> timesheets) throws ParseException;
	
	List<Timesheet> enregistrementTsByIds(UUID idUser,List<Timesheet> timesheets);
	
	void createDemande(UUID idUser,DemandeModificationTs demande);
	
	void confermationDemandeTS(UUID idUser, List<ConfermationTs> confermations);
	
	List<ModificationTs> findDemandeModificationTS(UUID idUser);
	
	Page<PKModifierTimesheet> findDemandeMTSByFilter(DemandeMTSFilter dmdMTS,int size,int page);
	
	List<Timesheet> createTimeSheetUser(UUID idUser, String dateD, String dateF, String periode) throws ParseException;
	
	Timesheet conferamtionTsEmp(UUID idUser, Timesheet timesheet);
	
	Timesheet enregistrementTsEmp(UUID idUser, Timesheet timesheet);
	
}
