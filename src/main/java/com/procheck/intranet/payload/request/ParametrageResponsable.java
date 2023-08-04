package com.procheck.intranet.payload.request;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ParametrageResponsable {
	
	
	public UUID id;
	@NotBlank
	public String organisme;
	public boolean active;
	
}
