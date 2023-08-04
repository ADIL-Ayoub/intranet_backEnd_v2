package com.procheck.intranet.models;


import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_type_demande", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class PKTypeDemande extends BaseEntity {
	
	@Column(name = "s_Type_Demande")
    private String codeTypeDemande;
	@Column(name = "s_name_type_Demande")
    private String nameTypeDemande;
	@Column(name = "n_Type_Demande")
    private Integer nTypeDemande;
	
	@OneToMany(mappedBy="typedemande")
	@JsonIgnore
    private Collection<PKDemande> demandeList;
	
}
