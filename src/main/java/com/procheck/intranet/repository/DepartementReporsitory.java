package com.procheck.intranet.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKDepartement;


@Repository
public interface DepartementReporsitory extends JpaRepository<PKDepartement, UUID>{

	
	Optional<PKDepartement> findByCodeDepartement(String code);

	Boolean existsByCodeDepartement(String code);

	Boolean existsByShortNameDepartement(String sShortNameClient);

	Optional<PKDepartement> findById(UUID id);

	Optional<PKDepartement> findByShortNameDepartement(String sShortNameClient);

}
