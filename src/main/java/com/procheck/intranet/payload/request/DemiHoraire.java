package com.procheck.intranet.payload.request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemiHoraire {

	private UUID Id;
	private String semaine;
	private String jourName;
	private LocalDate dateTimesheet;
	private String party1He;
	private String party1Hs;
	private String party2He;
	private String party2Hs;
	private String heursJour;
	private String heursTotal;
	private String heursNormal;
	private long heureSup;
	private boolean absent;
	private String absenceMotif;
	private String status;
	private int jourTravaille;
	private String warning;
	private String message;
	private boolean modifier;
	private boolean enabled;
	
	private List<TimsheetByProjet> projetTS=new ArrayList<TimsheetByProjet>();
	
	
	
}
