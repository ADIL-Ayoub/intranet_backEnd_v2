package com.procheck.intranet.models;


import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_etat_timesheet", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKEtatTimesheet extends BaseEntity{

	  	@Column(name = "s_code_etat_timesheet")
	    private String sCodeEtatTimesheet;
	    @Column(name = "s_name_etat_Timesheet")
	    private String sNameEtatTimesheet;
	    @Column(name = "n_etat_timesheet")
	    private Integer nEtatTimesheet;
	    @Column(name = "s_description_timesheet")
	    private String sDescriptionTimesheet;
	    
	    @ManyToMany(mappedBy = "etatTimesheets")
	    private Collection<PKTimesheet> TimesheetList;
}
