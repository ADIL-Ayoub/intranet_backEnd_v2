package com.procheck.intranet.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKCheck;



@Repository
public interface CheckRepository extends JpaRepository<PKCheck,UUID>{
	
	List<PKCheck> findAllByCode(UUID code);
	
	List<PKCheck> findTop3ByCodeOrderByVersionDesc(UUID code);
}