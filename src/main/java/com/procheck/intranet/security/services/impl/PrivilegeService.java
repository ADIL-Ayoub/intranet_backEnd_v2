package com.procheck.intranet.security.services.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import com.procheck.intranet.models.PKPrivilege;
import com.procheck.intranet.repository.PrivilegeRepository;
import com.procheck.intranet.security.services.IPrivilegeService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PrivilegeService implements IPrivilegeService{
	
	@Autowired
	PrivilegeRepository privilegeRepository;

	@Override
	public Page<PKPrivilege> findAll(Pageable pageable) {
		log.info("[ PRIVILEGE SERVICE ] ~ [ ALL PRIVILEGE ]");
		
		return privilegeRepository.findAll(pageable);
	}

	@Override
	public PKPrivilege findByName(String name) {
		log.info("[ PRIVILEGE SERVICE ] ~ [ GET  BY NAME  ]");
		PKPrivilege privilege= privilegeRepository.findByName(name);
		
		
		return privilege;
	}

	@Override
	public PKPrivilege createPrivilegeIfNotFound(String name,String code) {
		log.info("[ PRIVILEGE SERVICE ] ~ [ CREATE PRIVILEGE IF NOT FOUND ]");
		PKPrivilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new PKPrivilege(name,code);
            privilegeRepository.save(privilege);
        }
        return privilege;
	}

}
