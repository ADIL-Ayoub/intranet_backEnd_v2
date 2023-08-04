package com.procheck.intranet.services.specifications;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.procheck.intranet.models.PKPersonnel;
import com.procheck.intranet.payload.request.PersonnelFilter;
import com.procheck.intranet.repository.PersonnelReporsitory;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class PersonnelSpec {
	
	
	@Autowired
	private  PersonnelReporsitory personnelReporsitory;
	
	
	public  Specification<PKPersonnel> getFiltrePersonnel(PersonnelFilter personnel){
		
		return (root, criteriaQuery, criteriaBuilder) -> {
			
			List<Predicate> predicates=new ArrayList<>();
			
			 if (personnel.getCin() != null && !personnel.getCin().isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("cin"), personnel.getCin()));
	            }

	            if (personnel.getSNom()!= null && !personnel.getSNom().isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("sNom"), personnel.getSNom()));
	            }

	            if (personnel.getSPrenom() != null && !personnel.getSPrenom().isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("sPrenom"), personnel.getSPrenom()));
	            }
	            if (personnel.getSPoste() != null && !personnel.getSPoste().isEmpty()) {
	                predicates.add(criteriaBuilder.equal(root.get("sPoste"), personnel.getSPoste()));
	            }
	            if(!personnel.getAffectation().equals(null)) {
	            	predicates.add(criteriaBuilder.equal(root.get("bAffectation"), personnel.getAffectation()));
	            }
	            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sNom")));

	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	private  Page<PKPersonnel> findCandidaciesByPersonnel(PersonnelFilter personnel,PageRequest pageRequest) {
       
        int pageNumber =pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        
        Pageable pagereq = PageRequest.of(pageNumber,pageSize, Sort.by("sNom").ascending());
        
        Specification<PKPersonnel> personnelSpec=getFiltrePersonnel(personnel);
        
        Page<PKPersonnel> pageResult=personnelReporsitory.findAll(personnelSpec,pagereq);
        
        return new PageImpl<>(pageResult.getContent(),pageRequest,pageResult.getTotalElements());
	}
	
	public  Page<PKPersonnel> getPersonnelsByFilter(PersonnelFilter personnelFilter,int size,int page) {
		log.info("Begin getPersonnels input : [PersonnelFilter : CIN : {} NOM : {} PRENOM : {} POSTE : {} ] ",personnelFilter.getCin(),personnelFilter.getSNom(),personnelFilter.getSPrenom(),personnelFilter.getSPoste());
		Page<PKPersonnel> personnels = findCandidaciesByPersonnel(personnelFilter,PageRequest.of(Integer.valueOf(page), Integer.valueOf(size)));
		log.info("End getPersonnels input   : [PersonnelFilter : count : {} ]  ",personnels.getTotalElements());
		return personnels;
	}
	
}
