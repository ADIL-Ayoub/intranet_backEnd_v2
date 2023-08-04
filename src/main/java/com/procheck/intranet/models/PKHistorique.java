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
@Table(name = "t_historique", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKHistorique extends BaseEntity{
	
	@Column(name = "s_code_historique")
    private String sCodeHistorique;
	@Column(name = "d_date_action")
    private Date dDateAction;
	@Column(name = "s_action")
    private String sAction;
    
	
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdUser")
    private PKUser user;
}
