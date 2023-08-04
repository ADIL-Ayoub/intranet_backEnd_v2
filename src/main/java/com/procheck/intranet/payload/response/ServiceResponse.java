package com.procheck.intranet.payload.response;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceResponse {

	public UUID id;
	public String nameService;
}
