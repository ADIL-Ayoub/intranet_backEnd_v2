package com.procheck.intranet.models;



import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "t_pays", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
@ToString
public class PKPays  extends BaseEntity{

	@Column(name = "s_code_pays", unique = true, nullable = false)
	private String codePays;
	
	@Column(name = "s_label_pays", unique = true, nullable = false)
	private String labelPays;

	@OneToMany(mappedBy = "pays",cascade = CascadeType.ALL)
	Collection<PKJoureFerie> pJoureFerie;

	@OneToMany(mappedBy = "pkPays")
	@JsonIgnore
	private List<PKPersonnel> pkPersonnelList;
	
}
