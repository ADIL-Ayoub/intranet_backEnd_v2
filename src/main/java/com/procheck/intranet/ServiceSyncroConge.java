package com.procheck.intranet;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKConge;
import com.procheck.intranet.models.PKTimesheet;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.repository.TimesheetReporsitory;
import com.procheck.intranet.services.ICongeService;
import com.procheck.intranet.services.IDemandeService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.ITimesheetService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceSyncroConge {
	
	
	@Autowired
	ICongeService congeService;
	
	@Autowired
	IDemandeService demandeService;
	
	
	@Autowired
	ITimesheetService timesheetService;
	
	@Autowired
	TimesheetReporsitory timesheetReporsitory;
	
	@Autowired
	IPersonnelService personnelService;
	
	
	@Scheduled(cron ="0 0 1 * * ?")
	public void syncronisationConge() throws ParseException {
		log.info("start service syncro conge date : ",LocalDate.now());
		LocalDate localDate=LocalDate.now();
		
		List<PKConge> congesReprise=congeService.findByStatusAndDateReprise("validé", localDate);
		List<PKConge> congesDebut=congeService.findByStatusAndDateDebut("validé", localDate);
		
		if(!Objects.equals(congesReprise, null)) {
		
		for (PKConge conge : congesReprise) {

			log.info(conge.getDemande().getPersonnel().getNom()+" : conge "+ conge.getName() +" est close ");
				conge.setStatus("closé");
				congeService.save(conge);
				conge.getDemande().setStatus("closé");
				demandeService.save(conge.getDemande());
		}
		}
		
		if(!Objects.equals(congesDebut, null)) {
			
			for (PKConge conge : congesDebut) {
				log.info(conge.getDemande().getPersonnel().getNom()+" : conge "+ conge.getName() +" est valide ");
				
				long numOfDaysBetween = ChronoUnit.DAYS.between(conge.getDateDebut(), conge.getDateReprise());
				 List<LocalDate> dates=IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> conge.getDateDebut().plusDays(i))
						.collect(Collectors.toList());
				for (LocalDate date : dates) {
				
					log.info("DATE TIMESHEET CONGE :"+date);
					
					if(timesheetReporsitory.existsByDateTimesheetAndPersonnel_id(date, conge.getDemande().getPersonnel().getId())) {
						PKTimesheet time = timesheetReporsitory.findByDateTimesheetAndPersonnel_id(date,
								conge.getDemande().getPersonnel().getId());
						PKTimesheet timesheet=Outils.TimesheetConge(time, time.getPersonnel(),date);
						timesheetReporsitory.save(timesheet);
					}else {
						PKTimesheet time=new PKTimesheet();
						PKTimesheet timesheet=Outils.TimesheetConge(time, conge.getDemande().getPersonnel(),date);
						timesheetReporsitory.save(timesheet);
						
					}
					
				}
			}
		}
		log.info("fin service syncro conge ");
	}
	
//	@Scheduled(cron ="0 0 * 1 * ?")
//	public void syncronisationSoldeConge() {
//		
//	}
	
	
	
}
