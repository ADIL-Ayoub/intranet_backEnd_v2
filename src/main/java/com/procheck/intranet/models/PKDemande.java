package com.procheck.intranet.models;


import java.time.LocalDate;
import java.util.Collection;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.JoinColumn;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_demande", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKDemande extends BaseEntity{

    @Column(name = "s_code_demandeur")
    private String codeDemandeur;
    @Column(name = "d_date_creation")
    private LocalDate dDateCreation;
    @Column(name = "d_date_decision_sup")
    private LocalDate dDateDecisionSup;
    @Column(name = "s_decisionSup")
    private String decisionSup;
    @Column(name = "s_code_sup")
    private UUID codeSup;
    //    @Column(name = "d_date_decision_resp")
//    private LocalDate dDateDecisionResp;
//    @Column(name = "s_decisionResp")
//    private String decisionResp;
//    @Column(name = "s_code_resp")
//    private UUID sCodeResp;
//    @Column(name = "d_date_decision_rh")
//    private LocalDate dDateDecisionRH;
//    @Column(name = "s_decisionRh")
//    private String decisionRh;
//    @Column(name = "s_code_rh")
//    private UUID sCodeRh;
    @Column(name = "s_status")
    private String status;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdPersonnel", nullable=false)
    @JsonIgnore
    private PKPersonnel personnel;

//    @ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
//	@JoinTable(name="r_demande_etatDemande",joinColumns=@JoinColumn(name="demande_id"),
//	inverseJoinColumns=@JoinColumn(name="etatDemande_id"))
//    private Collection<PKEtatDemande> etatDemandes;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdTypeDemande", nullable=false)
    @JsonIgnore
    private PKTypeDemande typedemande;

    @OneToMany(mappedBy = "demande")
    @JsonIgnore
    private Collection<PKConge> conges;


    @OneToMany(mappedBy = "demande")
    @JsonIgnore
    private Collection<PKDocument> documents;


}
