package com.procheck.intranet.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.procheck.intranet.models.PKPays;

@Repository
public interface PaysReporsitory extends JpaRepository<PKPays, UUID> {

    @Query("select p from PKPays p where p.codePays = :scode")
    Optional<PKPays> findByCodePays(String scode);
    
    Boolean existsByLabelPays(String sShortNameClient);
    
    Boolean existsByCodePays(String codePays);
    
    

}
