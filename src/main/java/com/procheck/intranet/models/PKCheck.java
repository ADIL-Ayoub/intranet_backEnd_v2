package com.procheck.intranet.models;


import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t_check", schema = "public")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Data 
@AllArgsConstructor 
@NoArgsConstructor
public class PKCheck extends BaseEntity {
	
	
	@Column(name = "s_label")
	private String sLabel;
	
	
	@Column(name = "s_code")
	private UUID code;
	 

}
