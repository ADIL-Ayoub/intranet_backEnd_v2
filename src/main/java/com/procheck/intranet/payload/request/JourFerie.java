package com.procheck.intranet.payload.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JourFerie {

	public String name;
	public LocalDate date;
}
