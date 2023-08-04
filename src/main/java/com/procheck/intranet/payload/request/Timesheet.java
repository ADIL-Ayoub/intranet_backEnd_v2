package com.procheck.intranet.payload.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Timesheet {

	private UUID idPersonnel;
	private String nom;
	private String prenom;
	private String cin;
	private String poste;
	private String matrucule;
	private boolean projet;
	private String type;
	
	private List<DemiHoraire> horaires;
	
	
	
	
}
