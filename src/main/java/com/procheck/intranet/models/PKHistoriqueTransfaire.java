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
@Table(name = "t_historique_transfaire", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKHistoriqueTransfaire extends BaseEntity {
	
	@Column(name = "s_code_historique_transfaire")
    private String sCodeHistoriqueTransfaire;
	@Column(name = "s_created_by")
    private String sCreatedBy;
	@Column(name = "d_date_transfaire")
    private Date dDateTransfaire;
	@Column(name = "s_code_Entity_depart")
	private String sCodeEntityDepart;
	@Column(name = "s_code_Entity_arrive")
    private String sCodeEntityArriver;

	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdPersonnel")
    private PKPersonnel personnel;
   
}
