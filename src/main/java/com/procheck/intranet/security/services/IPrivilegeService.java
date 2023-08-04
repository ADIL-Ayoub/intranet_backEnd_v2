package com.procheck.intranet.security.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.procheck.intranet.models.PKPrivilege;


public interface IPrivilegeService {

	PKPrivilege createPrivilegeIfNotFound(String name,String code);
	
	Page<PKPrivilege> findAll(Pageable pageable);
	
	PKPrivilege findByName(String name);
	
	
}
