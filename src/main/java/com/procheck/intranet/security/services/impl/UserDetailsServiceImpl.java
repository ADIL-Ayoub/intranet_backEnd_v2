package com.procheck.intranet.security.services.impl;

import java.util.Calendar;

import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.procheck.intranet.models.PKCheck;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.payload.request.ChangePassword;

import com.procheck.intranet.repository.CheckRepository;
import com.procheck.intranet.repository.RoleRepository;
import com.procheck.intranet.repository.UserRepository;
import com.procheck.intranet.security.services.IUserDetailsService;
import com.procheck.intranet.services.IPersonnelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailsServiceImpl implements IUserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	IPersonnelService personnelService;
	
	@Autowired
	CheckRepository checkRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("[ USER SERVICE ] ~ [ LOAD USER BY LOGIN ]");
		PKUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

		return UserDetailsImpl.build(user);
	}

	@Override
	public PKUser findOne(UUID id) {
		log.info("[ USER SERVICE ] ~ [ GET ONE BY ID ]");

		PKUser user = userRepository.findById(id).get();

		return user;
	}

	@Override
	public PKUser findByEmail(String email) {
		log.info("[ USER SERVICE ] ~ [ GET ONE BY EMAIL ]");

		PKUser user = userRepository.findByEmail(email);

		return user;
	}

	@Override
	public PKUser updatePassword(String email, String password) {

		log.info("[ USER SERVICE ] ~ [ UPDATE PASSWORD BY Email ]");

		PKUser user = userRepository.findByEmail(email);

		password = passwordEncoder.encode(password);
		
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH, 2);
		user.setDExpiration(cal);

		user.setEnabled(true);
		user.setPassword(password);

		user = userRepository.save(user);
		
		checkRepository.save(new PKCheck(password, user.getId()));

		return user;
	}

	@Override
	public PKUser assignRoles(UUID id, List<PKRole> roles) {

		log.info("[ USER SERVICE ] ~ [ UPDATE ROLES BY ID ]");

		PKUser user = userRepository.findById(id).get();

		Set<PKRole> rolesSet = new HashSet<PKRole>();

		for (PKRole rol : roles) {

			PKRole role = roleRepository.findByName(rol.getName());

			rolesSet.add(role);
		}

		user.setRoles(rolesSet);
		user = userRepository.save(user);

		return user;
	}

	@Override
	public PKUser assigneRoleEmpGTS(UUID idUser) {
		log.info("[ USER SERVICE ] ~ [ ASSIGN ROLE EMP_GTS BY USER ]");
		
		PKUser user = userRepository.findById(idUser).get();
		
		Set<PKRole> rolesSet = new HashSet<PKRole>();
		
		PKRole role=roleRepository.findByName("EMP_GTS");
		
		rolesSet.add(role);
		
		user.getRoles().addAll(rolesSet);
		
		return userRepository.save(user);
	}
	
	
	@Override
	public void delete(UUID id) {
		log.info("[ USER SERVICE ] ~ [ DELETE USER BY ID ]");

		PKUser user = userRepository.getOne(id);

		userRepository.delete(user);
	}

	@Override
	public Page<PKUser> findAll(Pageable pageable) {
		log.info("[ USER SERVICE ] ~ [ ALL USERS ]");
		return userRepository.findAll(pageable);
	}

	@Override
	public PKUser updateById(UUID id, PKUser user) {
		log.info("[ USER SERVICE ] ~ [ UPDATE USER BY ID ]");

		if (userRepository.existsById(id)) {
			PKUser userD=userRepository.findById(id).get();
			
			
			userD.setPassword(passwordEncoder.encode(user.getPassword()));
			
			if(!userRepository.existsByEmail(user.getEmail())) {
				userD.setEmail(user.getEmail());
			}
			if(!userRepository.existsByUsername(user.getUsername())) {
				userD.setUsername(user.getUsername());
			}
			
			userD.setRoles(user.getRoles());
			
			userD.setEnabled(true);
			userD.setFirstConx(false);
			
			Calendar calCreation = Calendar.getInstance(); 
			Calendar calExperation = Calendar.getInstance(); 
			userD.setDCreation(calCreation);
			userD.setTentative(0);
			calExperation.add(Calendar.MONTH, 2);
			userD.setDExpiration(calExperation);
			
			
			user=userRepository.save(userD);
			
			checkRepository.save(new PKCheck(user.getPassword(), userD.getId()));
			
		}

		return user;
	}

	@Override
	public PKUser create(PKUser user) {
		log.info("[ USER SERVICE ] ~ [ CREATE USER ]");

		Calendar calCreation = Calendar.getInstance(); 
		Calendar calExperation = Calendar.getInstance(); 
		user.setDCreation(calCreation);
		user.setTentative(0);
		calExperation.add(Calendar.MONTH, 2);
		user.setDExpiration(calExperation);
		user.setEnabled(true);
		user.setFirstConx(true);
			
		
		
		return userRepository.save(user);
		
		
	}

	@Override
	public void updateisEnbled(UUID id) {
		log.info("[ USER SERVICE ] ~ [ LOCKED USER BY ID]");
		if (userRepository.existsById(id)) {
			
			PKUser userD=userRepository.findById(id).get();
			if(userD.isEnabled()) {
			userD.setEnabled(false);
			
			}else {
				userD.setEnabled(true);
				userD.setTentative(0);
				userD.setFirstConx(true);
			}
			
			userRepository.save(userD);
		}
		
	}

	@Override
	public void UpdateDateConx(String login) {
		log.info("[ USER SERVICE ] ~ [ UPDATE DATE CNX BY LOGIN]");
		
		PKUser user=userRepository.findByUsername(login).get();
		
		user.setDLastConx(Calendar.getInstance());
		
		user.setTentative(0);
		
		userRepository.save(user);
		
	}

	@Override
	public void updateEmailRoles(UUID id, PKUser user) {
		log.info("[ USER SERVICE ] ~ [ UPDATE EMAIL ROLES BY ID ]");

		if (userRepository.existsById(id)) {
			PKUser userD=userRepository.findById(id).get();
			
			if(!userRepository.existsByEmail(user.getEmail())) {
				userD.setEmail(user.getEmail());
			}
			if(!userRepository.existsByUsername(user.getUsername())) {
				userD.setUsername(user.getUsername());
			}
			
			userD.setRoles(user.getRoles());
			
			userD.setEnabled(true);
			userD.setFirstConx(false);
			
			Calendar calCreation = Calendar.getInstance(); 
			Calendar calExperation = Calendar.getInstance(); 
			userD.setDCreation(calCreation);
			userD.setTentative(0);
			calExperation.add(Calendar.MONTH, 2);
			userD.setDExpiration(calExperation);
			
			
			userRepository.save(userD);
			
			//checkRepository.save(new PKCheck(user.getPassword(), userD.getId()));
			
		}
	}

	@Override
	public PKUser changePassword(UUID id, ChangePassword user) {
		log.info("[ USER SERVICE ] ~ [ UPDATE PASSWORD BY ID ]");

	
			PKUser userD=userRepository.findById(id).get();
			
			userD.setPassword(passwordEncoder.encode(user.getPassword()));
			
			userD.setEnabled(true);
			userD.setFirstConx(false);
			
			Calendar calCreation = Calendar.getInstance(); 
			Calendar calExperation = Calendar.getInstance(); 
			userD.setDCreation(calCreation);
			userD.setTentative(0);
			calExperation.add(Calendar.MONTH, 2);
			userD.setDExpiration(calExperation);
			
			
			userRepository.save(userD);
			
			checkRepository.save(new PKCheck(user.getPassword(), userD.getId()));
			
		
		return userD;
	}

	@Override
	public PKUser findUserByIdPersonnel(UUID idPersonne) {
		log.info("[ USER SERVICE ] ~ [ FIND USER BY ID PERSONNEL ]");
		
		PKPersonnel personnel=personnelService.findPersonnelById(idPersonne);
		
		return personnel.getUser();
	}

	

}
