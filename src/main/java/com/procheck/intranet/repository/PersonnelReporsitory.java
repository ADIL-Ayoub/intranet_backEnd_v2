package com.procheck.intranet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.models.PKService;
import com.procheck.intranet.models.PKUser;

@Repository
public interface PersonnelReporsitory extends JpaRepository<PKPersonnel, UUID>,JpaSpecificationExecutor<PKPersonnel>{
	
	Optional<PKPersonnel> findByCin(String cin);
	
	Boolean existsByCin(String cin);
		
	List<PKPersonnel> findByService(PKService service);

	List<PKPersonnel> findByService_id(UUID id);
	
	PKPersonnel findByUser(PKUser user);

	//Mon code

	List<PKPersonnel> findBySuperieur(UUID superieur);

	@Query("FROM PKPersonnel p WHERE p.superieur = :superieur\n" +
			"AND (p.nom LIKE '%' || UPPER( :sNom )|| '%' OR p.nom LIKE '%' || LOWER( :sNom ) || '%' " +
			"OR p.prenom LIKE '%' || UPPER(:sPrenom) || '%' OR p.prenom LIKE '%' || lower(:sPrenom) || '%' " +
			"OR p.cin LIKE '%' || UPPER( :cin || '%') OR p.cin LIKE '%' || lower( :cin || '%') )")
	List<PKPersonnel> findBySuperieurAndNomOrPrenomOrCin(UUID superieur, String sNom, String sPrenom, String cin);

	
	
}
