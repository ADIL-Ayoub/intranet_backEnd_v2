package com.procheck.intranet.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PKPersonnWithTimeSheets {
	
	private PKPersonnel personn;
	private List<PKTimesheet> listTimeSheets;

}
