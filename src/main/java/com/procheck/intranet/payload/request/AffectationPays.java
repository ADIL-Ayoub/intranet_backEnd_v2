package com.procheck.intranet.payload.request;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AffectationPays {

	public UUID idPays;
	public List<UUID> idServies;
	
}
