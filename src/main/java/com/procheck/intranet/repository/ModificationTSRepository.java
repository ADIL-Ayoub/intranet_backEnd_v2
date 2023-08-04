package com.procheck.intranet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.procheck.intranet.models.PKModifierTimesheet;


public interface ModificationTSRepository extends JpaRepository<PKModifierTimesheet	, UUID>,JpaSpecificationExecutor<PKModifierTimesheet>{

	 List<PKModifierTimesheet> findByRecepteur(UUID code);
	 
	 Boolean existsByStatusAndTimesheet_id(String status,UUID idTimesheet);
	 
}
