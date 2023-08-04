package com.procheck.intranet.payload.request;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailTypeConge {
	
	UUID id;
	String label;
	int max;
	int min;
	
}
