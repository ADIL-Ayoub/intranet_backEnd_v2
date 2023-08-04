package com.procheck.intranet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKProjet;

@Repository
public interface ProjetReporsitory extends JpaRepository<PKProjet, UUID> {
	
	Boolean existsByCodeProjet(String code);
	
	List<PKProjet>  findByServices_id(UUID id);
	
	List<PKProjet> findByServices_idAndCodeProjetLike(UUID id,String Code);

}
