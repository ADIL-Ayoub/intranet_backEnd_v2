package com.procheck.intranet.models;


import java.util.Collection;

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
@Table(name = "t_client", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKClient extends BaseEntity {
	
	@Column(name = "s_code_client")
	private String codeClient;	
	@Column(name = "s_short_name_Client")
	private String shortNameClient;
	@Column(name = "s_long_name_client")
	private String longNameClient;
	@Column(name = "s_logo_client")
	private String logoClient;
	
	@Column(name = "s_code_semaine_travail")
	private String codeSemaineTravail;

	@JsonIgnore
	@OneToMany(mappedBy = "client")
	private Collection<PKService> services;
	
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name="nIdDepartement")
	private PKDepartement departement;
	
	
}
