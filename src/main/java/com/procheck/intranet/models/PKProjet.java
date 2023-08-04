package com.procheck.intranet.models;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_projet", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKProjet extends BaseEntity{
	
	@Column(name = "s_code_projet")
	private String codeProjet; 
	@Column(name = "s_short_name_projet")
	private String shortNameProjet;
	@Column(name = "s_perimetre")
	private String perimetre;
	
	@JsonIgnore
	@ManyToMany(mappedBy = "projets")
	private Collection<PKService> services;
	
	

}
