package com.procheck.intranet.payload.response;

import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {

	private MessageResponse messageResponse;
	private String token;
	private String type = "Bearer";
	private UUID id;
	private String username;
	private String email;
	private Calendar dExpiration;
	private Calendar dCreation;
	private Calendar dLastCnx;
	private boolean isEnabled;
	private boolean isFirstCnx;
	private Set<PKRole> roles;
	private PKPersonnel personne;
	

	public JwtResponse(String accessToken, UUID id, String username, String email,Calendar dExpiration,Calendar dCreation,Calendar dLastCnx,boolean isEnabled,
			boolean isFirstCnx,Set<PKRole> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.dExpiration=dExpiration;
		this.dCreation=dCreation;
		this.dLastCnx=dLastCnx;
		this.isEnabled=isEnabled;
		this.isFirstCnx=isFirstCnx;
		this.roles = roles;
		

	}

	public JwtResponse(String accessToken, UUID id, String username, String email,Calendar dExpiration,Calendar dCreation,Calendar dLastCnx,boolean isEnabled,
			boolean isFirstCnx,Set<PKRole> roles,MessageResponse messageResponse) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.dExpiration=dExpiration;
		this.dCreation=dCreation;
		this.dLastCnx=dLastCnx;
		this.isEnabled=isEnabled;
		this.isFirstCnx=isFirstCnx;
		this.roles = roles;
		this.messageResponse=messageResponse;

	}
	
	public JwtResponse(String accessToken, UUID id, String username, String email,Calendar dExpiration,Calendar dCreation,Calendar dLastCnx,boolean isEnabled,
			boolean isFirstCnx,Set<PKRole> roles,MessageResponse messageResponse,PKPersonnel personne) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.dExpiration=dExpiration;
		this.dCreation=dCreation;
		this.dLastCnx=dLastCnx;
		this.isEnabled=isEnabled;
		this.isFirstCnx=isFirstCnx;
		this.roles = roles;
		this.messageResponse=messageResponse;
		this.personne=personne;

	}
}
