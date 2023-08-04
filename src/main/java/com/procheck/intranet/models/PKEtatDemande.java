package com.procheck.intranet.models;


import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.ManyToMany;

import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_etat_demande", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKEtatDemande extends BaseEntity{
    
    @Column(name = "s_code_etat_demande")
    private String sCodeEtatDemande;
    @Column(name = "s_name_etat_demande")
    private String sNameEtatDemande;
    @Column(name = "n_etat_demande")
    private Integer nEtatDemande;
    @Column(name = "s_description_demande")
    private String sDescriptionDemande;
    
//    @ManyToMany(mappedBy = "etatDemandes")
//    private Collection<PKDemande> demandes;
//    
    
}
