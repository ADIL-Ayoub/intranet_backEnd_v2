package com.procheck.intranet.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

	@NotBlank
	private String username;

	@NotBlank
	@Email
	private String email;

	private Set<String> role;

	@NotBlank
	private String password;

	
	private String idPersonne; 

	
}
