package com.procheck.intranet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.procheck.intranet.outils.ImportFileCSV;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceImport {

	@Autowired
	ImportFileCSV importFileCSV;
	
	@Value("${path_in}")
	String pathIn;
	
	@Value("${path_out}")
	String pathOut;
	
	@Scheduled(cron ="0 0 * * * ?")
	public void lancerService() {
		
		log.info("start service");
		
		importFileCSV.applicationImport(pathIn, pathOut);
		
		log.info("fin service ");
		
	}
}
