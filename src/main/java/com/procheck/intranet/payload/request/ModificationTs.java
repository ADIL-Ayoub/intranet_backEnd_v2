package com.procheck.intranet.payload.request;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModificationTs {

	public UUID idDemande;
	public LocalDate dateDemande;
	public String employee;
	public LocalDate dateTS; 
	public String superviseur;
	public String status;
	public String service;
	
	
}
