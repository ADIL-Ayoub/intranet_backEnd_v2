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
@Table(name = "t_detail_conge", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PKDetailConge extends BaseEntity {

	@Column(name = "s_label")
	private String label;
	@Column(name = "n_max")
	private int max;
	@Column(name = "n_min")
	private int min;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "nIdTypeConge",insertable = true,updatable = true)
	private PKTypeConge typeConge;
	
	public PKDetailConge(String label) {
		this.label=label;
	}
}
