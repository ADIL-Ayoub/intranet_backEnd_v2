package com.procheck.intranet.models;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_type_document", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data @AllArgsConstructor @NoArgsConstructor
public class PKTypeDocument extends BaseEntity{

	@Column(name = "s_code_type_document")
	private String sCodeTypeDocument;	
	@Column(name = "s_type_document")
	private String sTypeDocument;
	
	@OneToMany(mappedBy = "typeDocument")
	private Collection<PKDocument> documents;
}
