package com.procheck.intranet.payload.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfosPersonne {

	public String name;
	public UUID  idBeneficiaire;
	public List<JourFerie> joursFeries;
	public List<JourTravail> jourTravail;
	public double solde;
	public List<LocalDate> dateConges;
	
}
