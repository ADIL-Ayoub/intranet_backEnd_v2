package com.procheck.intranet.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserEmailRoles {
	
	@NotBlank
	private String username;

	@NotBlank
	@Email
	private String email;

	private Set<String> roles;
	

}
