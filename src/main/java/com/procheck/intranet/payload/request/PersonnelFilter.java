package com.procheck.intranet.payload.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PersonnelFilter {

	
	//public UUID idPersonnel;
	public String cin;
	public String nom;
	public String prenom;
	public String sPoste;
	public Boolean affectation;
	
	
	public PersonnelFilter(String cin, String nom, String prenom, String poste) {
		this.cin=cin;
		this.nom =nom;
		this.prenom =prenom;
		this.sPoste=poste;

	}
}
