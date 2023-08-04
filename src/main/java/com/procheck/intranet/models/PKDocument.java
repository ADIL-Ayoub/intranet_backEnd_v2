package com.procheck.intranet.models;



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
@Table(name = "t_document", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKDocument extends BaseEntity{

	@Column(name = "s_code_document")
	private String sCodeDocument;	
	@Column(name = "s_name_document")
	private String sNameDocument;
	
	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdDemande")
	@JsonIgnore
    private PKDemande demande;
	
	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="nIdTypeDocument")
	@JsonIgnore
    private PKTypeDocument typeDocument;
	
}
