package com.procheck.intranet.payload.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Demande {
	
	public UUID idDemande;
	public String status;

}
