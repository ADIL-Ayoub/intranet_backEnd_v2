package com.procheck.intranet.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKJoureFerie;

@Repository
public interface JourFerieReporsitory extends JpaRepository<PKJoureFerie, UUID> {

	List<PKJoureFerie> findByPays_id(UUID id);

	Boolean existsByPays_idAndDateJoureFerie(UUID idPays,LocalDate date);
}
