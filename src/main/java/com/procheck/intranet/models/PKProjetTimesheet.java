package com.procheck.intranet.models;

import java.util.Calendar;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "t_timesheet_projet", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKProjetTimesheet extends BaseEntity{
	
	@Column(name = "s_projet")
	private String projet;
	@Column(name = "s_time")
	private String time;
	@Column(name = "s_description")
	private String description;
	
	@Column(name = "d_created_at")	
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dCreatedAt;
	
	@Column(name = "s_created_by")
	private UUID sCreatedBy;
	
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdTimesheet")
    private PKTimesheet timesheet;
}
