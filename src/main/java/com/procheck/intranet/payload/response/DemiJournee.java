package com.procheck.intranet.payload.response;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.payload.request.DemiHoraire;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemiJournee {

	private UUID idPersonnel;
	private String nom;
	private String prenom;
	
	private List<DemiHoraire> horaires;

}
