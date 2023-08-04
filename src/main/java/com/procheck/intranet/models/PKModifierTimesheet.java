package com.procheck.intranet.models;


import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_modifier_timesheet", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKModifierTimesheet extends BaseEntity {
	
	
	@Column(name = "code_demandeur")
	private String demandeur;
	
	@Column(name = "code_recepteur")
	private UUID recepteur;
	
	@Column(name = "s_moditf")
	private String typeDemande;
	@Column(name = "d_date_motif")
	private LocalDate dateDemande;
	@Column(name = "s_decision")
	private String status;
	
	@Column(name = "d_date_timesheet")
	private LocalDate dateTS;
	
	@Column(name = "s_name_employee")
	private String employee;
	
	
	@Column(name = "s_valider_by")
	private UUID sValiderBy;
	
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdTimesheet")
    @JsonIgnore
    private PKTimesheet  timesheet;
	
	
	
	
}
