package com.procheck.intranet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKHistorique;

@Repository
public interface HistoriqueReporsitory extends JpaRepository<PKHistorique,UUID>{

}
