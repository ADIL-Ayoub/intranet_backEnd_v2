package com.procheck.intranet.controllers;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKTypeConge;
import com.procheck.intranet.payload.request.TypeConge;
import com.procheck.intranet.payload.response.MessageResponse;
import com.procheck.intranet.repository.TypeCongeReporsitory;
import com.procheck.intranet.services.ITypeCongeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/typeConge")
public class TypeCongeController {
	
	@Autowired
	ITypeCongeService typeCongeService;
	
	@Autowired
	TypeCongeReporsitory typeCongeReporsitory;
	
	
	@GetMapping("/typeConges")
	@PreAuthorize("hasRole('find_typeConges')")
	public ResponseEntity<?> findAll() {

		log.info("[ TYPE CONGE CONTROLLER ] ~ [ FIND ALL TYPE CONGES ]");

		try {
			List<PKTypeConge> typeConges = typeCongeService.findAll();

			return new ResponseEntity<>(typeConges, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@PostMapping("/add")
	@PreAuthorize("hasRole('add_typeConges')")
	public ResponseEntity<?> save(@Valid @RequestBody PKTypeConge typeConge) {

		log.info("[ TYPE CONGE CONTROLLER ] ~ [ CREATE TYPE CONGES ]");

		try {
		
//		if(typeCongeReporsitory.existsByTypeConge(typeConge.getTypeConge())) {
//			return ResponseEntity.badRequest().body(new MessageResponse("Error: type conge is already taken!"));
//		}
			
		typeCongeService.save(typeConge);
		
        return new ResponseEntity<>(new MessageResponse("Type Conge registered successfully!"), HttpStatus.OK);

		}catch (Exception ex) {
		    log.error(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage() ,HttpStatus.INTERNAL_SERVER_ERROR);
        }

	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('findOne_typeConge')")
	public ResponseEntity<?> getOne(@PathVariable("id") UUID id) {

		log.info("[ TYPE CONGE CONTROLLER ] ~ [ GET TYPE CONGE BY ID ]");
		try {
			if (!typeCongeReporsitory.existsById(id)) {
				return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found type conge with id  !"));
			}
			PKTypeConge typeConge = typeCongeService.findTypeCongeById(id);

			return new ResponseEntity<>(typeConge, HttpStatus.OK);

		} catch (Exception ex) {
			log.error("ERROR : ", ex.getMessage());
			return new ResponseEntity<>("ERROR : " + ex.getMessage(), HttpStatus.NOT_FOUND);

		}
	}
	
	@PutMapping("/mod/{id}")
	@PreAuthorize("hasRole('update_typeConge')")
	public ResponseEntity<?> modifier(@PathVariable("id") UUID id,@RequestBody TypeConge typeConge) {
		log.info("[ TYPE CONGE CONTROLLER ] ~ [ UPDATE TYPE CONGE BY ID ]");
		
		if(!typeCongeReporsitory.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found type conge with id !"));

		}
		
		try {
//			if(typeCongeReporsitory.existsByTypeConge(typeConge.getTypeConge())) {
//				return ResponseEntity.badRequest().body(new MessageResponse("Error: type conge is already in use!"));
//			}
			
			typeCongeService.update(id,typeConge);
			
            return new ResponseEntity<>(new MessageResponse("Type conge registered successfully!"), HttpStatus.OK);

			
		} catch (Exception ex) {
		    log.error(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage() ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}
	
	@DeleteMapping("/del/{id}")
	@PreAuthorize("hasRole('delete_typeConge')")
	public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
		log.info("[ TYPE CONGE CONTROLLER ] ~ [ DELETE TYPE CONGE ]");
		if (!typeCongeReporsitory.existsById(id)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Not found type conge with id !"));
		}
		try {
			
			typeCongeService.delete(typeCongeService.findTypeCongeById(id));
			return ResponseEntity.ok(new MessageResponse("type conge delete successfully!"));
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
