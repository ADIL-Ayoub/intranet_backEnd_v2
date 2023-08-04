package com.procheck.intranet.models;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_privileges")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
public class PKPrivilege extends BaseEntity{

	@Column(name = "s_name")
	private String name;
	
	@Column(name = "s_code")
	private String code;
	
	public PKPrivilege(String name,String code) {
		this.name=name;
		this.code=code;
	}
}
