package com.procheck.intranet.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKDetailConge;
import com.procheck.intranet.repository.DetailCongeRepository;
import com.procheck.intranet.services.IDetailCongeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DetailCongeServiceImpl implements IDetailCongeService {
	
	@Autowired
	DetailCongeRepository detailCongeRepository;
	
	
	@Override
	public void save(PKDetailConge detailConge) {
		log.info("[ DETAIL CONGE SERVICE ] ~ [ SAVE DETAIL CONGE]");
		detailCongeRepository.save(detailConge);
	}

	@Override
	public List<PKDetailConge> findAll() {
		log.info("[ DETAIL CONGE SERVICE ] ~ [ FIND ALL DETAIL CONGE]");
		return detailCongeRepository.findAll();
	}

	@Override
	public PKDetailConge findDetailCongeById(UUID id) {
		log.info("[ DETAIL CONGE SERVICE ] ~ [ FIND DETAIL CONGE BY ID]");
		return detailCongeRepository.findById(id).get();
	}

	@Override
	public PKDetailConge update(PKDetailConge detailConge) {
		log.info("[ DETAIL CONGE SERVICE ] ~ [ UPDATE DETAIL CONGE]");
		PKDetailConge dConge=findDetailCongeById(detailConge.getId());
		
		dConge.setLabel(detailConge.getLabel());
		dConge.setMax(detailConge.getMax());
		dConge.setMin(detailConge.getMin());
		
		return detailCongeRepository.save(dConge);
		
	}

	@Override
	public void delete(PKDetailConge detailConge) {
		log.info("[ DETAIL CONGE SERVICE ] ~ [ DELETE DETAIL CONGE]");
		detailCongeRepository.delete(detailConge);
	}

}
