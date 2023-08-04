package com.procheck.intranet.payload.request;


import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimsheetByProjet {

	public UUID id;
	public String projet;
	public String time;
	public String description;
	public UUID idTimesheet;

}
