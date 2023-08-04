package com.procheck.intranet.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKEtatDemande;

@Repository
public interface EtatDemandeReporsitory extends JpaRepository<PKEtatDemande, UUID>{

}
