package com.procheck.intranet.models;


import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_jours_feries_annee", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKJoursFerieAnnee extends BaseEntity{
	
	@Column(name = "s_code_jours_ferie_annee")
	private String sCodeJoursFerieAnnee;
	@Column(name = "s_annee")
	private String sAnnee;

	
//	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
//	@JoinTable(name="r_jour_annee_jour_ferie",joinColumns=@JoinColumn(name="jour_annee_id"),
//	inverseJoinColumns=@JoinColumn(name="jour_ferie_id"))
//    private Collection<PKJoureFerie> joureFeries;
	
	
}
