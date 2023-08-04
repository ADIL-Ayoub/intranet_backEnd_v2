package com.procheck.intranet.payload.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Conge {

	public String name;
	public UUID typeDemande;
	public UUID idPersonne;
	public UUID typeConge;
	public String dateDebut;
	public String dateReprise;
	public String description;
	
}
