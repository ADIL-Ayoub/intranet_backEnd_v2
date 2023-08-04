package com.procheck.intranet.services.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKHoraire;
import com.procheck.intranet.repository.HoraireRepository;
import com.procheck.intranet.services.IHoraireService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HoraireServiceImpl implements IHoraireService{
	
	@Autowired
	HoraireRepository horaireRepository;

	@Override
	public PKHoraire findByJourIgnoreCaseAndSemaineTravails_Id(String jour, UUID idSemaine) {
		log.info("[SERVICE HORAIRE] [FIND HORAIRE BY JOUR AND ID SEMAINE]");
		return horaireRepository.findByJourIgnoreCaseAndSemaineTravails_Id(jour, idSemaine);
	}
	


	
}
