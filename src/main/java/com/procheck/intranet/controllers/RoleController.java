package com.procheck.intranet.controllers;


import java.util.List;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.PrivilegeRepository;
import com.procheck.intranet.repository.RoleRepository;
import com.procheck.intranet.security.services.IPrivilegeService;
import com.procheck.intranet.security.services.IRoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/role")
public class RoleController {

	@Autowired
	RoleRepository rolerepository;

	@Autowired
	IRoleService roleservice;
	
	@Autowired
	IPrivilegeService privilegeservice;
	
	@Autowired
	PrivilegeRepository privilegeRepository;

	@GetMapping("/roles")
	@PreAuthorize("hasRole('findAll_roles')")
	public ResponseEntity<?> all() {

		log.info("[ ROLE CONTROLLER ] ~ [ GET ALL ]");
		try {

			List<PKRole> roles = roleservice.findAll();

			return new ResponseEntity<>(roles, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('findOne_role')")
	public ResponseEntity<?> getOne(@PathVariable("id") UUID id) {

		log.info("[ ROLE CONTROLLER ] ~ [ GET ROLE BY ID ]");
		try {
			if (!rolerepository.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found role with id  !"));
			}
			PKRole role = roleservice.findOne(id);

			return new ResponseEntity<>(role, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}
	
	@PutMapping("/mod/{id}")
	@PreAuthorize("hasRole('update_role')")
	public ResponseEntity<?> modifier(@PathVariable("id") UUID id,@RequestBody PKRole role) {
		log.info("[ ROLE CONTROLLER ] ~ [ UPDATE ROLE BY ID ]");
		
		if(!rolerepository.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found role with id !"));

		}
		
		try {
			if(rolerepository.existsByName(role.getName())) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: role is already in use!"));
			}
			
			roleservice.update(id,role.getName());
			
            return new ResponseEntity<>(new MessageResponse("Role registered successfully!"), HttpStatus.OK);

			
		} catch (Exception ex) {
		    log.error(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage() ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	
	@PostMapping("/add")
	@PreAuthorize("hasRole('add_role')")
	public ResponseEntity<?> add(@Valid @RequestBody PKRole role){
		log.info("[ ROLE CONTROLLER ] ~ [ CREATE ROLE ]");
		
		
		try {
			if(rolerepository.existsByName(role.getName())) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: role is already in use!"));
			}
			
			roleservice.save(role);
			
            return new ResponseEntity<>(new MessageResponse("Role registered successfully!"), HttpStatus.OK);

			
		} catch (Exception ex) {
		    log.error(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage() ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PutMapping("/assign/{id}")
	@PreAuthorize("hasRole('assign_role_privilage')")
	public ResponseEntity<?> aasignPrivilege(@PathVariable("id") UUID id,@RequestBody List<UUID> privileges) {
		log.info("[ ROLE CONTROLLER ] ~ [ ASSIGN PRIVILEGE BY ID ]");
	try {
		if(!rolerepository.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found role with id !"));

		}
		
		roleservice.assignPrivilege(id, privileges);
		
		return new ResponseEntity<>(new MessageResponse("Role registered successfully!"), HttpStatus.OK);
		
	} catch (Exception ex) {
		   log.error(ex.getMessage());
           return new ResponseEntity<>(ex.getMessage() ,HttpStatus.INTERNAL_SERVER_ERROR);
       }
	}
}
