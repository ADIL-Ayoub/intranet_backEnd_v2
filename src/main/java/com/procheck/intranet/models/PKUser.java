package com.procheck.intranet.models;

import java.util.Calendar;

import java.util.HashSet;
import java.util.Set;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_users")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PKUser extends BaseEntity {

	@NotBlank
	@Column(name = "s_user_name", unique = true, nullable = false)
	private String username;

	@NotBlank
	@Column(name = "s_email", unique = true, nullable = false)
	@Email
	private String email;

	@NotBlank
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "s_password")
	private String password;
	
	
	@Column(name = "d_creation")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dCreation;
	
	@Column(name = "d_expiration")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dExpiration;
	
	@Column(name = "d_last_cnx")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dLastConx;
	
	@Column(name = "n_tentative")
	private int tentative;
	

	@Column(name = "is_Enabled")
	private boolean isEnabled;
	
	@Column(name = "is_First_cnx")
	private boolean isFirstConx;
	
	
	@Column(name = "is_affecte")
	private boolean isAffecte;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "r_user_roles", joinColumns = @JoinColumn(name = "user_id"),
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<PKRole> roles = new HashSet<>();
	
	
	@OneToOne(mappedBy = "user")
	private PKPersonnel personnel;
	

	public PKUser(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public PKUser(String username, String email, String password, Set<PKRole> roles) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public PKUser(String username, String email, String password,boolean isEnabled) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.isEnabled = isEnabled;

	}
	
	public PKUser(String username,String email,Set<PKRole> roles) {
		this.username=username;
		this.email=email;
		this.roles=roles;
	}
}
