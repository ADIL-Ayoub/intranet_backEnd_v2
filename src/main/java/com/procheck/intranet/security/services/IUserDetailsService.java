package com.procheck.intranet.security.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.procheck.intranet.models.PKRole;
import com.procheck.intranet.models.PKUser;
import com.procheck.intranet.payload.request.ChangePassword;
import com.procheck.intranet.payload.request.UserEmailRoles;

public interface IUserDetailsService extends UserDetailsService{
    
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
	
	Page<PKUser> findAll(Pageable pageable);
    PKUser findOne(UUID id);
    PKUser findByEmail(String email);
    PKUser updatePassword(String email,String password);
    PKUser updateById(UUID id,PKUser user);
    void updateEmailRoles(UUID id,PKUser user);
    
    PKUser changePassword(UUID id,ChangePassword user);
    
    void updateisEnbled(UUID id);
    
    PKUser create(PKUser user);
    
    PKUser assignRoles(UUID id,List<PKRole> roles);
    
    PKUser assigneRoleEmpGTS(UUID idUser);
    
    void delete(UUID id);
    
    void UpdateDateConx(String login);
    
    PKUser findUserByIdPersonnel(UUID idPersonne);
    
    
    
}
