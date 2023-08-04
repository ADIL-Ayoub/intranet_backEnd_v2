package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKDocument;

public interface IDocumentService {

	void save(PKDocument document);
	
	List<PKDocument> findAll();
	
	PKDocument findDocumentById(UUID id);
	
	PKDocument update(PKDocument document);
	
	void delete(PKDocument document);
}
