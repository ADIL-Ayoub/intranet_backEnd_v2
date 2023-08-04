package com.procheck.intranet.payload.response;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {

	public UUID id;
	public String shortNameClient;
	public List<ServiceResponse> services;
}
