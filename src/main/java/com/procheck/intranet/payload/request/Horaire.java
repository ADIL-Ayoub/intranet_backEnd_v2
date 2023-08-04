package com.procheck.intranet.payload.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Horaire {

	private UUID Id;
	private LocalDate dateTimesheet;
	private String party1He;
	private String party2Hs;
	private long heursJour;
	private long heureSup;
	private boolean absent;
	private String absenceMotif;
	private String status;
	private int jourTravaille;
}
