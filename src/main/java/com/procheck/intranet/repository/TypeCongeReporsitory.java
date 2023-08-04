package com.procheck.intranet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKTypeConge;

@Repository
public interface TypeCongeReporsitory extends JpaRepository<PKTypeConge, UUID> {

	Boolean existsByTypeConge(String typeConge);
}
