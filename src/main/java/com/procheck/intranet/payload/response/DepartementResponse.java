package com.procheck.intranet.payload.response;

import java.util.List;
import java.util.UUID;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartementResponse {

	public UUID id;
	public String shortNameDepartement;
	public List<Client> clients;
}
