package com.procheck.intranet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
	
	
	
}
