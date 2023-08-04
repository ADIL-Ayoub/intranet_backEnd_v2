package com.procheck.intranet.outils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.procheck.intranet.models.PKClient;
import com.procheck.intranet.models.PKDepartement;
import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKService;
import com.procheck.intranet.repository.ClientReporsitory;
import com.procheck.intranet.repository.DepartementReporsitory;
import com.procheck.intranet.repository.PersonnelReporsitory;
import com.procheck.intranet.repository.ServiceReporsitory;
import com.procheck.intranet.services.IClientService;
import com.procheck.intranet.services.IDepartementService;
import com.procheck.intranet.services.IPersonnelService;
import com.procheck.intranet.services.IServiceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ImportFileCSV {

	@Autowired
	private IDepartementService departementService;

	@Autowired
	private DepartementReporsitory departementReporsitory;

	@Autowired
	private IServiceService serviceService;

	@Autowired
	private ServiceReporsitory serviceReporsitory;

	@Autowired
	private IClientService clientService;

	@Autowired
	private ClientReporsitory clientReporsitory;
	
	@Autowired
	private IPersonnelService personnelService;
	
	@Autowired
	private PersonnelReporsitory personnelReporsitory;

	
	
	public void applicationImport(String pathFolder,String pathArchive) {
		try {
//			String pathFolder="C:/Users/cmso/Desktop/Extraction";
//			String pathArchive="C:/Users/cmso/Desktop/Extraction/Archive";
			
			File archive=new File(pathArchive);			
			File folder=new File(pathFolder);
			
			log.info(""+folder.getPath());
			
			if(folder.listFiles() != null) {
			
				for (File   folder_societe: folder.listFiles()) {
					
					if(folder_societe.isDirectory()) {
						
						log.info("folder_societe.getName : "+folder_societe.getName());
						
						for (File fileCSV: folder_societe.listFiles()) {
							
							log.info("societe.getAbsolutePath : "+fileCSV.getAbsolutePath());
							
								readFileServiceFromCSV(fileCSV.getAbsolutePath(),folder_societe.getName());
								DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
								String dateTimeInfo = dateFormat.format(new Date());
								File dest=new File(archive.getAbsoluteFile()+"/" + fileCSV.getName()+dateTimeInfo);
								
								fileCSV.renameTo(dest);
					}
					}
					
				
			
				}
			}else {
				log.error("aucun dossier dans le chemin : "+folder.getPath());
			}
			
		} catch (Exception e) {
			
			System.err.println(e.getMessage());
			
		}
	
	}
	
	
	public void readFileServiceFromCSV(String fileName,String societe) {

		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {

			String line = br.readLine();

			int i = 0;

			while (line != null) {

				if (i > 1) {

					String[] attributes = line.split(";");

					PKDepartement departement = createDepartement(attributes);
					PKClient client = createClient(attributes);
					PKService service = createService(attributes);
					PKPersonnel personnel = createPersonnel(attributes);

					if (departement != null) {

						if (!departementReporsitory.existsByCodeDepartement(departement.getCodeDepartement())) {

							departementService.save(departement);
							log.info("insert departement :"+ departement.getCodeDepartement());

						}
						PKDepartement departement2 = departementService
								.findDepartementByCode(departement.getCodeDepartement());
						client.setDepartement(departement2);

						if (!clientReporsitory.existsByCodeClientAndDepartement(client.getCodeClient(), departement2)) {

							clientService.save(client);
							log.info("insert client :"+ client.getCodeClient());
						}

						PKClient client2 = clientService.findClientByCode(client.getCodeClient());
						service.setClient(client2);

						if (!serviceReporsitory.existsByCodeServiceAndClient(service.getCodeService(), client2)) {

							serviceService.save(service);
							log.info("insert service :"+ service.getCodeService());
						}
						
						PKService service2 = serviceService.findServiceByCode(service.getCodeService());
						personnel.setService(service2);
						
						
						if(personnelReporsitory.existsByCin(personnel.getCin())) {
						
							PKPersonnel personnel2 = personnelService.findPersonnelByCin(personnel.getCin());
							
							personnel2.setSCivilite(personnel.getSCivilite());
							personnel2.setSAdresse1(personnel.getSAdresse1());
							personnel2.setSAdresse2(personnel.getSAdresse2());
							personnel2.setSCodePostal(personnel.getSCodePostal());
							personnel2.setSTelephone(personnel.getSTelephone());
							personnel2.setSEmail(personnel.getSEmail());
							personnel2.setDDateDebutContrat(personnel.getDDateDebutContrat());
							personnel2.setDDateFinContrat(personnel.getDDateFinContrat());
							personnel2.setNNombreEnfant(personnel.getNNombreEnfant());
							personnel2.setSTypeContrat(personnel.getSTypeContrat());
							personnel2.setFNbJourConge(personnel.getFNbJourConge());
							personnel2.setSSociete(societe);
							personnel2.setService(service2);
							
							personnelService.save(personnel2);
							log.info("update personne :"+ personnel2.getSNom()+" : "+i);
						
						
						}else {
							
							personnel.setBAffectation(false);
							personnel.setBGenerationTs(false);
							personnel.setSTypeGenerationTs("Manuel");
							personnel.setBProjetTs(false);
							personnel.setSSociete(societe);
							personnel.setService(service2);
							personnelService.save(personnel);
							log.info("insert personne :"+ personnel.getSNom()+" : "+i);
							
						}
						
					}

				}
				line = br.readLine();
				i++;
			}

		} catch (Exception e) {
			log.error("error", e);
		}
	}

	
	 public  void csvToPersonnels(InputStream is,String societe) {
		 
		 log.info("read file csv sage {}",societe);
		    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset().displayName()));
		        CSVParser csvParser = new CSVParser(fileReader,
		            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

		    	String line = fileReader.readLine();

				int i = 0;

				while (line != null) {

					if (i > 1) {

						String[] attributes = line.split(";");

						PKDepartement departement = createDepartement(attributes);
						PKClient client = createClient(attributes);
						PKService service = createService(attributes);
						PKPersonnel personnel = createPersonnel(attributes);

						if (departement != null) {

							if (!departementReporsitory.existsByCodeDepartement(departement.getCodeDepartement())) {

								departementService.save(departement);
								log.info("insert departement :"+ departement.getCodeDepartement());

							}
							PKDepartement departement2 = departementService
									.findDepartementByCode(departement.getCodeDepartement());
							client.setDepartement(departement2);

							if (!clientReporsitory.existsByCodeClientAndDepartement(client.getCodeClient(), departement2)) {

								clientService.save(client);
								log.info("insert client :"+ client.getCodeClient());
							}

							PKClient client2 = clientService.findClientByCode(client.getCodeClient());
							service.setClient(client2);

							if (!serviceReporsitory.existsByCodeServiceAndClient(service.getCodeService(), client2)) {

								serviceService.save(service);
								log.info("insert service :"+ service.getCodeService());
							}
							
							PKService service2 = serviceService.findServiceByCode(service.getCodeService());
							personnel.setService(service2);
							
							
							if(personnelReporsitory.existsByCin(personnel.getCin())) {
							
								PKPersonnel personnel2 = personnelService.findPersonnelByCin(personnel.getCin());
								
								personnel2.setSCivilite(personnel.getSCivilite());
								personnel2.setSAdresse1(personnel.getSAdresse1());
								personnel2.setSAdresse2(personnel.getSAdresse2());
								personnel2.setSCodePostal(personnel.getSCodePostal());
								personnel2.setSTelephone(personnel.getSTelephone());
								personnel2.setSEmail(personnel.getSEmail());
								personnel2.setDDateDebutContrat(personnel.getDDateDebutContrat());
								personnel2.setDDateFinContrat(personnel.getDDateFinContrat());
								personnel2.setNNombreEnfant(personnel.getNNombreEnfant());
								personnel2.setSTypeContrat(personnel.getSTypeContrat());
								personnel2.setFNbJourConge(personnel.getFNbJourConge());
								personnel2.setSSociete(societe.toUpperCase());
								personnel2.setSPoste(personnel.getSPoste());
								personnel2.setService(service2);
								
								
								personnelService.save(personnel2);
								log.info("update personne :"+ personnel2.getSNom()+" : "+i);
							
							}else {
								personnel.setBAffectation(false);
								personnel.setType("jour");
								personnel.setBGenerationTs(false);
								personnel.setSTypeGenerationTs("Manuel");
								personnel.setBProjetTs(false);
								personnel.setSSociete(societe.toUpperCase());
								personnel.setService(service2);
								personnelService.save(personnel);
								log.info("insert personne :"+ personnel.getSNom()+" : "+i);
								
							}	
						}

					}
					line = fileReader.readLine();
					i++;
				}
		    	
		    } catch (IOException e) {
		      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		    }
	 }

	private PKPersonnel createPersonnel(String[] metadata) {
		
		SimpleDateFormat dateFormat =new  SimpleDateFormat("dd/MM/yy",Locale.ENGLISH);
		
		
		PKPersonnel personnel=new PKPersonnel();
		personnel.setSMatruculePaie(metadata[0].trim());
		personnel.setSCivilite(metadata[1].trim());
		personnel.setSNom(metadata[2].trim().toUpperCase());
		personnel.setSPrenom(metadata[3].trim().toUpperCase());
		personnel.setSAdresse1(metadata[4].trim());
		personnel.setSAdresse2(metadata[5].trim());
		personnel.setSCodePostal(metadata[6].trim());
		personnel.setSPays(metadata[7].trim());
		personnel.setSTelephone(metadata[8].trim());
		personnel.setSEmail(metadata[9].trim());
		personnel.setCin(metadata[10].trim().toUpperCase());
		
		Date dateN=null;
		try {
			dateN=dateFormat.parse(metadata[11].trim());
			log.info("date naissance :"+dateN);
			personnel.setDDateNaissance(dateN);
		} catch (Exception e) {
			log.info("date naissance :"+e.getMessage());
			personnel.setDDateNaissance(null);
		}
		try{
		int n=Integer.parseInt(metadata[12].trim());
		personnel.setNNombreEnfant(n);
		}
        catch (NumberFormatException ex){
        	log.error("type incorrecte pour Nombre Enfant"+ex.getMessage());
            ex.printStackTrace();
        }
		personnel.setSTypeContrat(metadata[19].trim());
		personnel.setSPoste(metadata[20].trim().toUpperCase());
		try {
			personnel.setDDateDebutContrat(dateFormat.parse(metadata[21].trim()));
		} catch (Exception e) {
			personnel.setDDateDebutContrat(null);
		}
		try {
			personnel.setDDateFinContrat(dateFormat.parse(metadata[22].trim()));
		} catch (Exception e) {
			personnel.setDDateFinContrat(null);
		}
		try{
		String val=metadata[24].trim();
		Double d=new Double(val.replace(",", "."));
		log.info("metadata[24].trim() : "+d);
		personnel.setFNbJourConge(d);
		personnel.setFNbRestConge(d);
		}
        catch (NumberFormatException ex){
        	log.error("type incorrecte pour solde conge : "+ex.getMessage());
            ex.printStackTrace();
        }

		return personnel;
	}

	private static PKService createService(String[] metadata) {

		PKService service = new PKService();
		service.setCodeService(metadata[18].trim());
		service.setNameService(metadata[17].trim());
		service.setActiveRespo(false);

		return service;

	}

	private static PKClient createClient(String[] metadata) {

		PKClient client = new PKClient();
		client.setCodeClient(metadata[16].trim());
		client.setShortNameClient(metadata[15].trim());

		return client;

	}

	private static PKDepartement createDepartement(String[] metadata) {

		PKDepartement departement = new PKDepartement();
		departement.setCodeDepartement(metadata[14].trim());
		departement.setShortNameDepartement(metadata[13].trim());


		return departement;

	}

}
