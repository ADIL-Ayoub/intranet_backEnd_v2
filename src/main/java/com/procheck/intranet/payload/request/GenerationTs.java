package com.procheck.intranet.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GenerationTs {

	
	public boolean fGenerationTs;

	@NotBlank
	public String sTypeGenerationTs;

	
	public  boolean fProjetTs;
	
	@NotBlank
	public String type;
	

}
