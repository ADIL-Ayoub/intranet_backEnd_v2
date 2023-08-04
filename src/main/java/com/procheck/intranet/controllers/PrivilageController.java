package com.procheck.intranet.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKPrivilege;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.PrivilegeRepository;
import com.procheck.intranet.security.services.IPrivilegeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/privilege")
public class PrivilageController {
	
	@Autowired
	IPrivilegeService privilegeService;
	
	@Autowired
	PrivilegeRepository privilegeRepository;
	
	@GetMapping("/privileges")
	@PreAuthorize("hasRole('findAll_privileges')")
	public ResponseEntity<?> all(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.info("[ PRIVILEGES CONTROLLER ] ~ [ GET ALL ]");
		try {

			Page<PKPrivilege> privileges = privilegeService.findAll(PageRequest.of(page, size));

			return new ResponseEntity<>(privileges, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('findOne_privilege')")
	public ResponseEntity<?> getOne(@PathVariable("id") UUID id) {

		log.info("[ PRIVILEGE CONTROLLER ] ~ [ GET PRIVILEGE BY ID ]");
		try {
			if (!privilegeRepository.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found privilege with id !"));
			}
			PKPrivilege privilege = privilegeRepository.findById(id).get();

			return new ResponseEntity<>(privilege, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}
	
	@PutMapping("/mod/{id}")
	@PreAuthorize("hasRole('update_privilege')")
	public ResponseEntity<?> modifier(@PathVariable("id") UUID id,@RequestBody PKPrivilege privilege) {
		log.info("[ PRIVILEGE CONTROLLER ] ~ [ UPDATE PRIVILEGE BY ID ]");
		
		if (!privilegeRepository.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found privilege with id  !"));
		}
		
		try {
			PKPrivilege privilegeP=privilegeRepository.findById(id).get();
		
			privilegeP.setName(privilege.getName());
			
			privilegeRepository.save(privilegeP);
			
			return new ResponseEntity<>(new MessageResponse("privilege registered successfully!"), HttpStatus.OK);
		} catch (Exception ex) {
			   log.error(ex.getMessage());
	            return new ResponseEntity<>(ex.getMessage() ,HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	}
}
