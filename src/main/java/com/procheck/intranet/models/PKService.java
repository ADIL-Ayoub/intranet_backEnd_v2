package com.procheck.intranet.models;


import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "t_service", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKService extends BaseEntity {
	
    @Column(name = "s_code_service")
    private String codeService;
    @Column(name = "s_nam_service")
    private String nameService;
    /*
     * Cas voir
     */
    @Column(name = "id_responsable")
    private UUID codeResponsable;
    @Column(name = "id_superviseur")
    private UUID codeSuperviseur;
    
    @Column(name = "s_code_semaine_travail")
	private String codeSemaineTravail;
    
    @Column(name = "b_resp_validation")
    private Boolean activeRespo; 
    
    
    @JsonIgnore
    @OneToMany(mappedBy="service",fetch = FetchType.EAGER)
    private Collection<PKPersonnel> personnels;
    
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdClient")
    private PKClient client;
    
    
    @JsonIgnore
    @ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
   	@JoinTable(name="r_service_projet",joinColumns=@JoinColumn(name="service_id"),
   	inverseJoinColumns=@JoinColumn(name="projet_id"))
    private List<PKProjet> projets;
}
