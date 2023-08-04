package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKDetailConge;


public interface IDetailCongeService {

	void save(PKDetailConge detailConge);

	List<PKDetailConge> findAll();

	PKDetailConge findDetailCongeById(UUID id);

	PKDetailConge update(PKDetailConge detailConge);

	void delete(PKDetailConge detailConge);
}
