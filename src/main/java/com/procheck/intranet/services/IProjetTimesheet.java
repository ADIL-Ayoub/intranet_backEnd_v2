package com.procheck.intranet.services;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKProjetTimesheet;
import com.procheck.intranet.payload.request.TimsheetByProjet;

public interface IProjetTimesheet {

	
	String save(UUID id,List<TimsheetByProjet> projetTimesheet) throws ParseException;
	
	PKProjetTimesheet update(UUID id,TimsheetByProjet projetTimesheet);
	
	void delete(UUID id);
	
	boolean existsById(UUID id);
}
