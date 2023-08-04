package com.procheck.intranet.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKJoureFerie;
import com.procheck.intranet.repository.JourFerieReporsitory;
import com.procheck.intranet.services.IJourFerieService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JourFerieServiceImpl implements IJourFerieService{
	
	
	@Autowired
	JourFerieReporsitory jourFerieReporsitory;
	
	@Override
	public void save(PKJoureFerie jourFerie) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PKJoureFerie> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PKJoureFerie findJourFerieById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PKJoureFerie update(PKJoureFerie jourFerie) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(PKJoureFerie jourFerie) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<PKJoureFerie> findJoursFeriesByPaysId(UUID idPays) {
		log.info("[SERVICE JOUR FERIE] [FIND JOUR FERIE BY ID]");
		return jourFerieReporsitory.findByPays_id(idPays);
	}

}
