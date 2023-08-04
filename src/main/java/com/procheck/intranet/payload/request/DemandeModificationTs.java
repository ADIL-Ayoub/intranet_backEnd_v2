package com.procheck.intranet.payload.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandeModificationTs {

	public UUID idTimesheet;
	public boolean modifier;
	
	
}
