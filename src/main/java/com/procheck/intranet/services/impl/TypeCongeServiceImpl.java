package com.procheck.intranet.services.impl;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKDetailConge;
import com.procheck.intranet.models.PKTypeConge;
import com.procheck.intranet.payload.request.DetailTypeConge;
import com.procheck.intranet.payload.request.TypeConge;
import com.procheck.intranet.repository.DetailCongeRepository;
import com.procheck.intranet.repository.TypeCongeReporsitory;
import com.procheck.intranet.services.IDetailCongeService;
import com.procheck.intranet.services.ITypeCongeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TypeCongeServiceImpl implements ITypeCongeService {

	@Autowired
	TypeCongeReporsitory typeCongeReporsitory;
	
	@Autowired
	DetailCongeRepository detailCongeRepository;
	
	@Autowired
	IDetailCongeService detailCongeService;
	

	@Override
	public void save(PKTypeConge typeConge) {
		log.info("[ TYPE CONGE SERVICE ] ~ [ SAVE TYPE CONGE]");
		PKTypeConge typeConge2=typeCongeReporsitory.save(typeConge);
		for (PKDetailConge ditailconge : typeConge.getDetaileConges()) {
			ditailconge.setTypeConge(typeConge2);
			detailCongeService.save(ditailconge);
		}
	}

	@Override
	public List<PKTypeConge> findAll() {
		log.info("[ TYPE CONGE SERVICE ] ~ [ FIND ALL TYPE CONGE]");
		return typeCongeReporsitory.findAll();
	}

	@Override
	public PKTypeConge findTypeCongeById(UUID id) {
		log.info("[ TYPE CONGE SERVICE ] ~ [ FIND TYPE CONGE BY ID]");
		return typeCongeReporsitory.findById(id).get();
	}

	@Override
	@Transactional
	public PKTypeConge update(UUID id,TypeConge typeConges) {
		log.info("[ TYPE CONGE SERVICE ] ~ [ UPDATE TYPE CONGE]");
		PKTypeConge tc=findTypeCongeById(id);
		tc.setTypeConge(typeConges.getTypeConge());
		tc.setMax(typeConges.getMax());
		tc.setMin(typeConges.getMin());

		for (DetailTypeConge detailTypeConge	 : typeConges.getDetaileConges()) {
			if(detailCongeRepository.existsById(detailTypeConge.getId())) {
			PKDetailConge dt=detailCongeRepository.findById(detailTypeConge.getId()).get();
			dt.setLabel(detailTypeConge.getLabel());
			dt.setMax(detailTypeConge.getMax());
			dt.setMin(detailTypeConge.getMin());
			dt.setTypeConge(tc);
			detailCongeService.save(dt);
			}else {
				PKDetailConge dtNew=new PKDetailConge();
				dtNew.setLabel(detailTypeConge.getLabel());
				dtNew.setMax(detailTypeConge.getMax());
				dtNew.setMin(detailTypeConge.getMin());
				dtNew.setTypeConge(tc);
				detailCongeService.save(dtNew);
			}
		}
		return typeCongeReporsitory.save(tc);
	}

	@Override
	public void delete(PKTypeConge typeConge) {
		log.info("[ TYPE CONGE SERVICE ] ~ [ DELETE TYPE CONGE]");
		
		List<PKDetailConge> detailsconges=detailCongeRepository.findByTypeConge(typeConge);
		for (PKDetailConge pkDetailConge : detailsconges) {
			
			detailCongeRepository.delete(pkDetailConge);
		}
		
		typeCongeReporsitory.delete(typeConge);

	}

	

}
