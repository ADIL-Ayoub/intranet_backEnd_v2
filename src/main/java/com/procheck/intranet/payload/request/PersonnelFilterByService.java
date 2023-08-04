package com.procheck.intranet.payload.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonnelFilterByService {
	
	public List<UUID> idServices;
	public String cin;
	public String sNom;
	public String sPrenom;
	public String sMatruculePaie;
	public String sPoste;
	public Boolean bProjetTs;
}
