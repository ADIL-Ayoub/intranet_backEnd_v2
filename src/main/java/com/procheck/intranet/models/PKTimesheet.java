package com.procheck.intranet.models;


import java.time.LocalDate;
import java.util.Calendar;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_timesheet", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKTimesheet extends BaseEntity {
	
	@Column(name = "s_code_timesheet")
	private String sCodeTimesheet;
	
	@Column(name = "d_date_timesheet")
	//@Temporal(TemporalType.TIMESTAMP)
	private LocalDate dateTimesheet;
	
	@Column(name = "s_party1_he")
	private String sParty1He;
	@Column(name = "s_party1_hs")
	private String sParty1Hs;
	@Column(name = "s_party2_he")
	private String sParty2He;
	@Column(name = "s_party2_hs")
	private String sParty2Hs;
	
	@Column(name = "s_heure_sup")
	private long nHeureSup;
	
	@Column(name = "s_created_by")
	private UUID sCreatedBy;
	
	@Column(name = "s_valider_by")
	private UUID sValiderBy;
	
	@Column(name = "d_created_at")	
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dCreatedAt;
	
	@Column(name = "n_absent")
	private boolean nAbsent;
	@Column(name = "s_absence_type")
	private String sAbsenceType;
	@Column(name = "s_absence_motif")
	private String sAbsenceMotif;
	
	@Column(name = "s_status")
	private String status;
	
	@Column(name = "f_jour_travail")
	private int fJourTravaille;
	
	@Column(name = "n_heure_travail")
	private String nHeureTravaille;
	
	@Column(name = "n_heure_total")
	private String nHeureTotale;
	
	@Column(name = "n_absent_party")
	private int nAbsentParty;
	
	@Column(name = "id_service")
	private UUID service;
	
	@Column(name = "s_semaine")
	private String semaine;
	
	@Column(name = "b_modifier")
	private boolean bModifier;
	
	@Column(name = "b_enabled")
	private boolean bEnabled;
	
	@ManyToOne
    @JoinColumn(name="nIdPersonnel")
    private PKPersonnel personnel;
	
   
	@JsonIgnore
	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinTable(name="r_timesheet_etatTimesheet",joinColumns=@JoinColumn(name="Timesheet_id"),
	inverseJoinColumns=@JoinColumn(name="etatTimesheet_id"))
	private Collection<PKEtatTimesheet> etatTimesheets;
	
	@JsonIgnore
	@OneToMany(mappedBy="timesheet")
    private Collection<PKHistoriqueTimesheet> historiquetimesheets;
	
	
	
	@OneToMany(mappedBy="timesheet")
    private List<PKProjetTimesheet> projetTimesheets;
	
	
}
