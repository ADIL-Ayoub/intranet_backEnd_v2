package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKTypeDocument;

public interface ITypeDocumentService {

	void save(PKTypeDocument typeDocument);
	
	List<PKTypeDocument> findAll();
	
	PKTypeDocument findTypeDocumentById(UUID id);
	
	PKTypeDocument update(PKTypeDocument typeDocument);
	
	void delete(PKTypeDocument typeDocument);
	
}
