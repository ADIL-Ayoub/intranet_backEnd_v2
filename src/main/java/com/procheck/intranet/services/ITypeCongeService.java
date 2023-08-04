package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKTypeConge;
import com.procheck.intranet.payload.request.TypeConge;

public interface ITypeCongeService {

	void save(PKTypeConge typeConge);

	List<PKTypeConge> findAll();

	PKTypeConge findTypeCongeById(UUID id);

	PKTypeConge update(UUID id,TypeConge typeConge);

	void delete(PKTypeConge typeConge);
	
	
}
