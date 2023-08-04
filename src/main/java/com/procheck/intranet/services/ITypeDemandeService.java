package com.procheck.intranet.services;

import java.util.List;
import java.util.UUID;

import com.procheck.intranet.models.PKTypeDemande;

public interface ITypeDemandeService {

	PKTypeDemande save(PKTypeDemande typeDemande);

	List<PKTypeDemande> findAll();

	PKTypeDemande findTypeDemandeById(UUID id);

	PKTypeDemande update(PKTypeDemande typeDemande);

	void delete(PKTypeDemande typeDemande);
	
	PKTypeDemande findTypeDemandeByCode(String code);
}
