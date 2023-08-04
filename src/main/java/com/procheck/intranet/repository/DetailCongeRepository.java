package com.procheck.intranet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKDetailConge;
import com.procheck.intranet.models.PKTypeConge;

@Repository
public interface DetailCongeRepository extends JpaRepository<PKDetailConge, UUID>{

	List<PKDetailConge> findByTypeConge(PKTypeConge typeConge);
	
	Boolean existsByIdAndTypeConge(UUID id,PKTypeConge typeConge);
}
