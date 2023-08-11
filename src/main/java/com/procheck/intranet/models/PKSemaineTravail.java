package com.procheck.intranet.models;


import java.util.Collection;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name = "t_Semaine_travail", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
@ToString
public class PKSemaineTravail extends BaseEntity {
	
	@Column(name = "s_code_jour_travail")
	private String sCodeSemaineTravail;
	@Column(name = "s_label")
	private String sSemaine;
	
	@Column(name = "s_status")
	private String status;
	
	@OneToMany(mappedBy="semaineTravail")
	@JsonIgnore
    private Collection<PKPersonnel> personnels;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name="r_semaine_horaire",joinColumns=@JoinColumn(name="semaine_id"),
	inverseJoinColumns=@JoinColumn(name="horaire_id"))
    private List<PKHoraire> horaires;
	
}
