package com.procheck.intranet.payload.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DemandeFilter {
	
	public UUID codeSuper;
	public String matricule;
	public String nom;
	public String prenom;
	public String status;
	public LocalDate dateDemande;
	
}
