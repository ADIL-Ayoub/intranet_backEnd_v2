package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKPays;
import com.procheck.intranet.payload.request.AffectationPays;

public interface IPaysService {
	
	PKPays findPaysById(UUID id);

	void affecationEmployees(AffectationPays modele);
	
	List<PKPays> findAll();
	
	
}
