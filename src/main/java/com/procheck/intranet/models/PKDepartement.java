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
@Table(name = "t_departement", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKDepartement extends BaseEntity{
	
	@Column(name = "s_code_departement")
	private String codeDepartement;	
	@Column(name = "s_short_name_departement")
	private String shortNameDepartement;
	@Column(name = "s_long_name_departement")
	private String longNameDepartement;
	
	@Column(name = "s_code_semaine_travail")
	private String codeSemaineTravail;
	
	@JsonIgnore
	@OneToMany(mappedBy = "departement")
	private Collection<PKClient> clients;
}
