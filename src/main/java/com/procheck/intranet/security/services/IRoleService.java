package com.procheck.intranet.security.services;


import java.util.List;
import java.util.UUID;


import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKUser;

public interface IRoleService {

	PKRole createRoleIfNotFound(String name);
	
	List<PKRole> findAll();
	
	PKRole update(UUID id,String role);
	
	PKRole assignPrivilege(UUID id,List<UUID> privileges);
	
	void delete (UUID id);
	
	PKRole findOne(UUID id);
	
	PKRole save(PKRole role);
	
	PKRole findRoleByName(String name);
	
	List<String> getNameRoleByUser(PKUser user);
	
	List<PKRole> findRolesByIds(List<UUID> ids);
	
	
}
