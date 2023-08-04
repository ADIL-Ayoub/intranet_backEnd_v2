package com.procheck.intranet.security.services.impl;


import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.procheck.intranet.models.PKPrivilege;
import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKUser;



public class UserDetailsImpl implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
	private UUID id;

	private String username;

	private String email;

	@JsonIgnore
	private String password;
	
	private Calendar dExpiration;
	
	private Calendar dCreation;
	
	private Calendar dLastCnx;
	
	private boolean isEnabled;
	
	private boolean isFirstCnx;
	
	private Set<PKRole> roles;
	
	private Collection<? extends GrantedAuthority> authorities;
	
	public UserDetailsImpl(UUID id, String username, String email, String password,Calendar dExpiration,Calendar dCreation,Calendar dLastCnx,Collection<? extends GrantedAuthority> authorities,
			boolean isEnabled,boolean isFirstCnx,Set<PKRole> roles) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.roles = roles;
		this.dExpiration=dExpiration;
		this.dCreation=dCreation;
		this.dLastCnx=dLastCnx;
		this.isEnabled=isEnabled;
		this.isFirstCnx=isFirstCnx;
		
	}
	
	

	public static UserDetailsImpl build(PKUser user) {

		return new UserDetailsImpl(
				user.getId(), 
				user.getUsername(), 
				user.getEmail(),
				user.getPassword(),
				user.getDExpiration(),
				user.getDCreation(),
				user.getDLastConx(),
				getAuthorities(user),
				user.isEnabled(),
				user.isFirstConx(),
				user.getRoles());
	}

	
	private static Collection<SimpleGrantedAuthority> getAuthorities(PKUser user){
		
		  Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
		  
		  for (PKRole role : user.getRoles()) {
			
			  for (PKPrivilege 	privilege : role.getPrivileges()) {

				  authorities.add(new SimpleGrantedAuthority(privilege.getCode()));
				  
				  //log.info("UserDetailsImpl : getAuthorities ",privilege.getCode());
			}
		}
		  
		  return authorities;
	}
	
	
	public Set<PKRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<PKRole> roles) {
		this.roles = roles;
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return authorities;
	}
	
	public UUID getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}
	

	public Calendar getdExpiration() {
		return dExpiration;
	}


	public Calendar getdCreation() {
		return dCreation;
	}



	public Calendar getdLastCnx() {
		return dLastCnx;
	}



	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public String getUsername() {
		
		return username;
	}
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}
	

	public boolean isFirstCnx() {
		return isFirstCnx;
	}


	@Override
	public boolean isAccountNonExpired() {
	
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}


}
