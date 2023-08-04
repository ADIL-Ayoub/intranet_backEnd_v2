package com.procheck.intranet.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePassword {

	@NotBlank
	public String username;

	@NotBlank
	public String passwordOld;
	
	@NotBlank
	public String password;
	
	@NotBlank
	public String passwordConfirme;
}
