package com.procheck.intranet.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.procheck.intranet.outils.ImportFileCSV;
import com.procheck.intranet.payload.response.MessageResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/import")
public class ImportController {
	
	
	@Autowired
	ImportFileCSV importFileCSV;
	
	@PostMapping("/fileImportCSV")
	@PreAuthorize("hasRole('importcsv_sage')")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		
		log.info(" type :{} Name :{} ",file.getContentType(),file.getOriginalFilename());
//		boolean name=file.getOriginalFilename().endsWith(".csv");
//		log.info("check : "+name);
//		if(Outils.hasCSVFormat(file)) {
//			if(Outils.checkExtention(file.getOriginalFilename())) {
//		log.info("check csv  :"+Outils.checkExtention(file.getOriginalFilename()));
		if(file.getOriginalFilename().endsWith(".csv")) {
		
			try {
				if(file.getOriginalFilename().toLowerCase().contains("procheck")){
					importFileCSV.csvToPersonnels(file.getInputStream(), "PROCHECK");
				}else if(file.getOriginalFilename().toLowerCase().contains("orone")) {
					importFileCSV.csvToPersonnels(file.getInputStream(), "ORONE");
				}else if(file.getOriginalFilename().toLowerCase().contains("sagma")) {
					importFileCSV.csvToPersonnels(file.getInputStream(), "SAGMA");
				}else {
					return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("name file incorrect : " + file.getOriginalFilename()));

				}
				return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Uploaded the file successfully: " + file.getOriginalFilename()));
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse("Could not upload the file: " + file.getOriginalFilename() + "! Exception :"+e));
			}
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Could not upload the file: " + file.getOriginalFilename() + "!"));
		
		
	}
	

}
