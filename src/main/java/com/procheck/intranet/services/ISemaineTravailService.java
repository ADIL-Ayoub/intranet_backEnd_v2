package com.procheck.intranet.services;

import java.util.UUID;


import com.procheck.intranet.models.PKSemaineTravail;
import com.procheck.intranet.payload.request.AffectationSemaine;

public interface ISemaineTravailService {

	PKSemaineTravail findByIdPersonnel(UUID personnels);
	PKSemaineTravail findByPersonnels_idAndHoraireByDate(UUID id,String date);
	void affecationEmployees(AffectationSemaine modele);
	
}
