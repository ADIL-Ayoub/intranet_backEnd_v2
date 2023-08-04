package com.procheck.intranet.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKTypeDemande;
import com.procheck.intranet.repository.TypeDemandeReporsitory;
import com.procheck.intranet.services.ITypeDemandeService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TypeDemandeServiceImpl implements ITypeDemandeService{
	
	
	@Autowired
	TypeDemandeReporsitory typeDemandeReporsitory;
	
	@Override
	public PKTypeDemande save(PKTypeDemande typeDemande) {
		log.info("[ TYPE DEMANDE SERVICE ] ~ [ SAVE TYPE DEMANDE ]");
		return typeDemandeReporsitory.save(typeDemande);
	}

	@Override
	public List<PKTypeDemande> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PKTypeDemande findTypeDemandeById(UUID id) {
		log.info("[ TYPE DEMANDE SERVICE ] ~ [ FIND TYPE DEMANDE BY ID]");
		return typeDemandeReporsitory.findById(id).get();
	}

	@Override
	public PKTypeDemande update(PKTypeDemande typeDemande) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(PKTypeDemande typeDemande) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PKTypeDemande findTypeDemandeByCode(String code) {
		log.info("[ TYPE DEMANDE SERVICE ] ~ [ FIND TYPE DEMANDE BY CODE]");
		return typeDemandeReporsitory.findByCodeTypeDemande(code);
	}

	
	
}
