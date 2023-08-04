package com.procheck.intranet.payload.request;


import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DemandeMTSFilter {
	
	
	public UUID recepteur;
	public String status;
	public LocalDate dateDemande;
	public String typeDemande;
}
