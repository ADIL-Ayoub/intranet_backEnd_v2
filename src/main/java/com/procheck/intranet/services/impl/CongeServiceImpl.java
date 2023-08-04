package com.procheck.intranet.services.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKConge;

import com.procheck.intranet.payload.request.Conge;

import com.procheck.intranet.repository.CongeReporsitory;
import com.procheck.intranet.services.ICongeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CongeServiceImpl implements ICongeService{
	
	
	@Autowired
	CongeReporsitory congeReporsitory;

	
	@Override
	public void save(PKConge conge) {
		log.info("[ CONGE SERVICE ] ~ [ SAVE CONGE]");
		congeReporsitory.save(conge);
		
	}

	@Override
	public List<PKConge> findAll() {
		log.info("[ CONGE SERVICE ] ~ [ ALL CONGE]");
		return congeReporsitory.findAll();
	}

	@Override
	public PKConge findCongeById(UUID id) {
		log.info("[ CONGE SERVICE ] ~ [ FIND CONGE BY ID]");
		return congeReporsitory.findById(id).get();
	}

	@Override
	public PKConge update(PKConge conge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(PKConge conge) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Conge> enregistreConges(UUID idUser, List<Conge> conges) {
		log.info("[ CONGE SERVICE ] ~ [ ENREGISTRE DEMANDE CONGE]");
		for (Conge conge : conges) {
			PKConge c=new PKConge();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate startDate = LocalDate.parse(conge.getDateDebut(), formatter);
			LocalDate finDate = LocalDate.parse(conge.getDateReprise(), formatter);
			c.setDateDebut(startDate);
			c.setDateReprise(finDate);
//			c.getNombreJour()
			
		}
		return null;
	}

	@Override
	public List<PKConge> findByStatusAndDateReprise(String status, LocalDate date) {
		log.info("[ CONGE SERVICE ] ~ [  FIND BY STATUS AND DATE REPRISE]");
		return congeReporsitory.findByStatusAndDateReprise(status, date);
	}

	@Override
	public List<PKConge> findByStatusAndDateDebut(String status, LocalDate date) {
		log.info("[ CONGE SERVICE ] ~ [  FIND BY STATUS AND DATE DEBUT ]");
		return congeReporsitory.findByStatusAndDateDebut(status, date);
	}

	
}
