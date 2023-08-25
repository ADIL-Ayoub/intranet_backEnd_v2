package com.procheck.intranet.payload.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonnelFilterByService {
	
	public List<UUID> idServices;
	public String cin;
	public String nom;
	public String prenom;
	public String matruculePaie;
	public String poste;
	public Boolean projetTs;
}
