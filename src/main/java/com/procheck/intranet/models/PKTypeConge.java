
package com.procheck.intranet.models;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_type_conge", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKTypeConge extends BaseEntity {
	
	@Column(name = "s_Type_conge")
    private String typeConge;
	@Column(name = "n_max")
    private int max;
	@Column(name = "n_min")
    private int min;
	@Column(name = "b_heur")
	private boolean  heur;
	@Column(name = "b_jour")
	private boolean  jour;
    
	@OneToMany(cascade = CascadeType.MERGE,mappedBy="typeConge")
    private Collection<PKDetailConge> detaileConges;
	
	@JsonIgnore
	@OneToMany(mappedBy="typeConge")
    private Collection<PKConge> conges;
	
	public PKTypeConge(String typeConge) {
		this.typeConge=typeConge;
	}
	
	
    
   
}
