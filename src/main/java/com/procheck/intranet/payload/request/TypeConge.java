package com.procheck.intranet.payload.request;


import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeConge {

	String typeConge;
	int max;
	int min;
	boolean heur;
	boolean jour;
	
	Collection<DetailTypeConge> detaileConges;
}
