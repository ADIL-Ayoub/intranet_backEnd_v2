package com.procheck.intranet.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "t_historique_timesheet", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKHistoriqueTimesheet extends BaseEntity{

	
	@Column(name = "s_code_historique_timesheet")
	private String sCodeHistoriqueTimesheet;
	
	@Column(name = "s_code_timesheet")
	private String sCodeTimesheet;
	@Column(name = "d_date_timesheet")
	private Date dDateTimesheet;
	@Column(name = "s_party2_he")
	private String sParty2He;
	@Column(name = "s_party1_hs")
	private String sParty1Hs;
	@Column(name = "s_party1_he")
	private String sParty1He;
	@Column(name = "s_party2_hs")
	private String sParty2Hs;
	@Column(name = "s_heure_sup")
	private long nHeureSup;
	@Column(name = "s_created_by")
	private String sCreatedBy;
	@Column(name = "s_created_at")
	private Date dCreatedAt;
	@Column(name = "n_absent")
	private int nAbsent;
	@Column(name = "s_absence_type")
	private String sAbsenceType;
	@Column(name = "s_absence_motif")
	private String sAbsenceMotif;
	@Column(name = "s_status")
	private int nStatus;
	@Column(name = "f_jour_travail")
	private Double fJourTravaille;
	@Column(name = "n_heure_travail")
	private long nHeureTravaille;
	@Column(name = "n_absent_party")
	private int nAbsentParty;
	
	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdTimesheet")
    private PKTimesheet timesheet;
}
