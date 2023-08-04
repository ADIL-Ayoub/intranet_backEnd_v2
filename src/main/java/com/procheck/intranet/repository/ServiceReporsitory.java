package com.procheck.intranet.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKClient;

import com.procheck.intranet.models.PKService;

@Repository
public interface ServiceReporsitory extends JpaRepository<PKService, UUID>{

	Optional<PKService> findByCodeService(String code);
	
	Optional<PKService> findByCodeServiceAndClient(String code,PKClient client);

	Boolean existsByCodeService(String code);

	Boolean existsByNameService(String nameService);
	
	Boolean existsByIdAndCodeResponsable(UUID idService,UUID idResponsable);
	
	Boolean existsByIdAndCodeSuperviseur(UUID idService,UUID idSuperviseur);
	
	Boolean existsByCodeServiceAndClient(String nameService,PKClient client);

	Optional<PKService> findById(UUID id);

	Optional<PKService> findByNameService(String name);
	
//	@Query("select p from PKService p where p.codeResponsable = :scode")
//    List<PKService> findByCodeResponsable(String scode);
	
	List<PKService> findByCodeResponsable(UUID idResponsable);
	
	List<PKService> findByCodeSuperviseur(UUID idSuperviseur);
	
	Boolean existsByCodeResponsable(UUID idResponsable);

	Boolean existsByCodeSuperviseur(UUID idSuperviseur);
	
	Page<PKService> findByClient_id(UUID idClient,Pageable pageable);
	
	List<PKService> findByClient_id(UUID idClient);
}
