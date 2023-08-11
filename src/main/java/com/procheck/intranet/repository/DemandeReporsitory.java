package com.procheck.intranet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKDemande;
import com.procheck.intranet.models.PKPersonnel;

@Repository
public interface DemandeReporsitory extends JpaRepository<PKDemande, UUID>,JpaSpecificationExecutor<PKDemande> {
	
	List<PKDemande> findByCodeDemandeurAndTypedemande_idAndConges_status(String userName,UUID idTypeDemande,String status);
	
	List<PKDemande> findByPersonnel_idAndTypedemande_id(UUID idPersonne,UUID idTypeDemande);
	
	List<PKDemande> findByCodeSupAndTypedemande_id(UUID idSuper,UUID idTypeDemande);

	List<PKDemande> findByCodeSup(UUID codeSup);
	List<PKDemande> findAllByCodeSup(UUID codeSup);

	
	

}
