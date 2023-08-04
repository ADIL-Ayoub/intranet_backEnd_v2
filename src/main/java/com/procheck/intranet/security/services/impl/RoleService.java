package com.procheck.intranet.security.services.impl;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKPrivilege;
import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.PrivilegeRepository;
import com.procheck.intranet.repository.RoleRepository;
import com.procheck.intranet.security.services.IRoleService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class RoleService implements IRoleService {
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PrivilegeRepository privilegeRepository;

	@Override
	public PKRole createRoleIfNotFound(String name) {
		log.info("[ ROLE SERVICE ] ~ [ CREATE IF NOT FOUND ]");
		PKRole role = roleRepository.findByName(name);
        if (role == null) {
//            role = new PKRole(name);
//            role.setPrivileges(privileges);
//            roleRepository.save(role);
        	new MessageResponse("ROLE NOT FOUND");
        }
        return role;
	}

	@Override
	public List<PKRole> findAll() {
		log.info("[ ROLE SERVICE ] ~ [ GET ALL ]");
		
		List<PKRole> roles=roleRepository.findAll();
		
		return roles;
	}

	@Override
	public PKRole update(UUID id,String role) {
		log.info("[ ROLE SERVICE ] ~ [ UPDATE ROLE ]");
		
		PKRole roleD=roleRepository.getOne(id);
		
		if(roleD!=null && !roleRepository.existsByName(role)) {
			
		roleD.setName(role);
		
		roleD = roleRepository.save(roleD);
		}
		
		return roleD;
	}

	@Override
	public PKRole assignPrivilege(UUID id, List<UUID> privileges) {
		
		log.info("[ ROLE SERVICE ] ~ [ UPDATE PRIVILEGES BY ID]");
		
		PKRole role = roleRepository.findById(id).get();
		
		Set<PKPrivilege> privilegesSet=new HashSet<PKPrivilege>();
		
		for (UUID privilege : privileges) {
			
			if (privilegeRepository.existsById(privilege)) {
				
				
				privilegesSet.add(privilegeRepository.findById(privilege).get());	
			}
		}
		role.setPrivileges(privilegesSet);
		
		role=roleRepository.save(role);
		
		return role;
	}

	@Override
	public void delete(UUID id) {
		
		log.info("[ ROLE SERVICE ] ~ [ DELETE ROLE ]");
		
		PKRole role=roleRepository.getOne(id);
		
		roleRepository.delete(role);
		
	}

	@Override
	public PKRole findOne(UUID id) {
		log.info("[ ROLE SERVICE ] ~ [ GET ONE BY ID ]");

		PKRole role = roleRepository.findById(id).get();

		return role;
	}

	@Override
	public PKRole save(PKRole role) {
		log.info("[ ROLE SERVICE ] ~ [ CREATE ROLE ]");
		
		return roleRepository.save(role);
	}

	@Override
	public PKRole findRoleByName(String name) {
		log.info("[ ROLE SERVICE ] ~ [ FIND ROLE BY NAME ]");
		return roleRepository.findByName(name);
	}

	@Override
	public List<String> getNameRoleByUser(PKUser user) {
		log.info("[ ROLE SERVICE ] ~ [ FIND NAMES BY USER ]");
		Set<PKRole> roles=user.getRoles();
		List<String> names=new ArrayList<String>();
				
		for (PKRole pkRole : roles) {
			
			names.add(pkRole.getName());
		}
		
		return names;
	}

	@Override
	public List<PKRole> findRolesByIds(List<UUID> ids) {
		log.info("[ ROLE SERVICE ] ~ [ FIND ROLES BY IDS ]");
		return roleRepository.findAllById(ids);
	}

	
	
}
