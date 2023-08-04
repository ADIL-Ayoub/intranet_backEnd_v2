package com.procheck.intranet.models;


import java.util.Collection;
import java.util.Date;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_horaire", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKHoraire extends BaseEntity {
	
	@Column(name = "s_code_horaire")
	private String sCodeHoraire;
	@Column(name = "s_jour")
	private String jour;
	@Column(name = "s_party2_he")
	private String sParty2He;
	@Column(name = "s_party1_hs")
	private String sParty1Hs;
	@Column(name = "s_party1_he")
	private String sParty1He;
	@Column(name = "s_party2_hs")
	private String sParty2Hs;
	
	@Column(name = "f_dure_pause")
	private long durePause;
	@Column(name = "f_heur_journee")
	private String  heurJournee;
	
	
	@Column(name = "s_created_by")
	private String sCreatedBy;
	@Column(name = "d_created_at")
	private Date dCreatedAt;
	

	@ManyToMany(mappedBy="horaires",cascade = CascadeType.ALL)
	@JsonIgnore
    private Collection<PKSemaineTravail> semaineTravails;

}
