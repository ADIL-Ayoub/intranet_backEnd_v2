package com.procheck.intranet.controllers;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.procheck.intranet.models.PKTimesheet;
import com.procheck.intranet.outils.Outils;
import com.procheck.intranet.repository.TimesheetReporsitory;
import com.procheck.intranet.services.ITimesheetService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/intranet/export")
public class ExportController {
	
	@Autowired
	ITimesheetService serviceTimesheet;
	
	@Autowired
	TimesheetReporsitory reporsitorTimesheet;
	
	 @GetMapping("/download-timesheets-pdf")
	    public ResponseEntity<Resource> getPDFTimesheet(
	            @RequestHeader(name = "Content-disposition") final String fileName,
	            @RequestHeader(name = "Content-Type") final String mediaType) {
	        log.info("Downloading timsheets pdf");
	        final List<PKTimesheet> timesheets = reporsitorTimesheet.findAll();
	        final InputStreamResource resource = new InputStreamResource(Outils.load(timesheets));
	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, fileName)
	                .contentType(MediaType.parseMediaType(mediaType))
	                .body(resource);
	    }
	
}
