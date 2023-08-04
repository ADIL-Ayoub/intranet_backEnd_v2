package com.procheck.intranet.services;

import java.util.UUID;

import com.procheck.intranet.models.PKHoraire;


public interface IHoraireService {

	PKHoraire findByJourIgnoreCaseAndSemaineTravails_Id(String jour,UUID idSemaine);
	
}
