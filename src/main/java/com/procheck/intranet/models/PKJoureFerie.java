package com.procheck.intranet.models;


import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_jour_ferie", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKJoureFerie extends BaseEntity {
	
	@Column(name = "s_code_jour_ferie")
	private String sCodeJoureFerie;
	@Column(name = "d_date_jour_ferie")
	private LocalDate dateJoureFerie;
	@Column(name = "s_description_jour_ferie")
	private String sDescriptionJoureFerie;
	@Column(name = "n_fix")
	private int nFix;
	@Column(name = "s_created_by")
	private String created_by;
	
//	@ManyToMany(mappedBy = "joureFeries")
//	private Collection<PKJoursFerieAnnee> joursFerieAnnees;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="nIdPays")
	@JsonIgnore
    private PKPays pays;
	
	
}
