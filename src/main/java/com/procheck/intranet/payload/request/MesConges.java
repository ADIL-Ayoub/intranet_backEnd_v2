package com.procheck.intranet.payload.request;


import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesConges {

	public UUID idDemande;
	public String demandeur;
	public UUID idPersonnel;
	public UUID idTypeConge;
	public String nom;
	public String prenom;
	public String dateDemande;
	public String typeConge;
	public String dateDebut;
	public String dateReprise;
	public int nbrJours;
	public String status;
	//mon code
	public String nomConge;
	public String typeDemande;
	public String statutDemande;
	public String description;
	
}
