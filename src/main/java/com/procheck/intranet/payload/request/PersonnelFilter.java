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
	public String sNom;
	public String sPrenom;
	public String sPoste;
	public Boolean affectation;
	
	
	public PersonnelFilter(String cin, String nom, String prenom, String poste) {
		this.cin=cin;
		this.sNom=nom;
		this.sPrenom=prenom;
		this.sPoste=poste;

	}
}
